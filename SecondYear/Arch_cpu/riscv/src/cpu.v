// RISCV32I CPU top module
// port modification allowed for debugging purposes

module cpu(
    input  wire                 clk_in,			// system clock signal
    input  wire                 rst_in,			// reset signal
	input  wire					rdy_in,			// ready signal, pause cpu when low

    input  wire [ 7:0]          mem_din,		// data input bus
    output reg  [ 7:0]          mem_dout,		// data output bus
    output reg  [31:0]          mem_a,			// address bus (only 17:0 is used)
    output reg                  mem_wr,			// write/read signal (1 for write)

	output wire [31:0]			dbgreg_dout		// cpu register output (debugging demo)
);

// implementation goes here

// Specifications:
// - Pause cpu(freeze pc, registers, etc.) when rdy_in is low
// - Memory read takes 2 cycles(wait till next cycle), write takes 1 cycle(no need to wait)
// - Memory is of size 128KB, with valid address ranging from 0x0 to 0x20000
// - I/O port is mapped to address higher than 0x30000 (mem_a[17:16]==2'b11)
// - 0x30000 read: read a byte from input
// - 0x30000 write: write a byte to output (write 0x00 is ignored)
// - 0x30004 read: read clocks passed since cpu starts (in dword, 4 bytes)
// - 0x30004 write: indicates program stop (will output '\0' through uart tx)
reg [31:0] reg0 [31:0];
reg [31:0] regpc;
reg [4:0] instr_cnt; 
reg [31:0] tot_instr;

//cache
reg [48:0] cache [1023:0];	//32(data)+16(addr) +1(valid)=49
							//cache size = 4B * 4096 = 16KB
							//all cache size = 24KB < 32KB 
							//2_way
reg [10:0] cache_loc;
reg [11:0] cache_loc2;
reg [48:0] write_buffer,read_buffer;
reg store_place;

//cache io
reg [4:0] cache_busy;		//the busy from num 1..3 out
reg [4:0] cache_ready; 		//the cache step 31-ready in
reg cache_io;				//out
reg [17:0] cache_addr;		//out
reg [31:0] cache_out;		//out
reg [31:0] cache_in;		//in
reg [4:0] cache_len;		//out
	//if hits , return in one cycle (as fast as possible)

reg [32:0] instr_1,instr_2;//instr and valid
reg [31:0] reg_buf1,reg_buf2,reg_buf3;
reg [31:0] val1,val2,val3;
wire [31:0] res1;
reg [6:0] alu_opcode;
reg [4:0] pipe1_ready,pipe2_ready,pipe3_ready;
	//simulation
	integer i;
	initial begin
		for (i=0; i<1023; i=i+1) begin
			cache[i] = 0;
		end
		for (i=0; i<32; i=i+1) begin
			reg0[i] = 0;
		end
		//reg0[0] = 0;
		mem_wr = 0;
		mem_a = 0;
		mem_dout = 0;
		regpc = 0;
		instr_1 = 0;
		instr_2 = 0;
		instr_cnt = 0;
		alu_opcode = 0;
		pipe1_ready = 0;
		pipe2_ready = 0;
		pipe3_ready = 0;
		cache_busy = 0;
		cache_ready = 0;
	end
	//---simulation
	
	integer j,k;
	alu alu(alu_opcode,val1,val2,res1);
	always @(posedge clk_in)
	  begin
	///		
		if (rst_in) begin
		  for (j=0;j<1024;j=j+1)begin
		      cache[j][48] <= 0;
		  end
		end else	
			
    if ((cache_busy != 0)&&cache_ready != 31) begin
        //assume that it is memory option 
        if (cache_addr[17]==0) begin
            if ((cache[cache_addr[11:2]][48] == 1)&&(cache[cache_addr[11:2]][47:32] == cache_addr[17:2])) begin
                //$display("cache_io:%d",cache_addr);
                if (cache_io) begin
                    case (cache_len)
                        5'b00001:begin
                            case (cache_addr[1:0])
                                2'b00:cache[cache_addr[11:2]] [ 7: 0] <= cache_out[7:0];
                                2'b01:cache[cache_addr[11:2]] [15: 8] <= cache_out[7:0];
                                2'b10:cache[cache_addr[11:2]] [23:16] <= cache_out[7:0];
                                2'b11:cache[cache_addr[11:2]] [31:24] <= cache_out[7:0];
                            endcase
                        end 
                        5'b00010:begin
                            case (cache_addr[1:0])
                                2'b00:cache[cache_addr[11:2]] [15: 0] <= cache_out[15:0];
                                2'b10:cache[cache_addr[11:2]] [31:16] <= cache_out[15:0];
                            endcase
                        end 
                        5'b00100: begin
                            cache[cache_addr[11:2]][31:0] <= cache_out[31:0];
                        end 
                    endcase
                    cache_in <= cache_out;
                end 
                else begin
                    case (cache_len)
                        5'b00001:begin
                            case (cache_addr[1:0])
                                2'b00:cache_in[7:0] <= cache[cache_addr[11:2]] [7:0];
                                2'b01:cache_in[7:0] <= cache[cache_addr[11:2]] [15:8];
                                2'b10:cache_in[7:0] <= cache[cache_addr[11:2]] [23:16];
                                2'b11:cache_in[7:0] <= cache[cache_addr[11:2]] [31:24];
                            endcase
                            cache_in[31:8] <= 0;
                        end
                        5'b00010:begin
                            case (cache_addr[1:0])
                                2'b00:cache_in[15:0] <= cache[cache_addr[11:2]] [15:0];
                                2'b10:cache_in[15:0] <= cache[cache_addr[11:2]] [31:16];
                            endcase
                            cache_in[31:16] <= 0;
                        end
                        5'b00100:begin
                            cache_in[31:0] <= cache[cache_addr[11:2]] [31:0];
                            //$display(cache[cache_addr[11:2]]);
                        end
                    endcase
                end
                cache_ready <= 31;
            end 
            else if (cache[cache_addr[11:2]][48] == 1) begin
                case (cache_ready)
                    0: begin
                        mem_a <= {cache[cache_addr[11:2]][47:32],2'b00};
                        mem_wr <= 1;
                        mem_dout <= cache[cache_addr[11:2]][7:0];
                    end
                    1: begin
                        mem_a <= {cache[cache_addr[11:2]][47:32],2'b01};
                        mem_wr <= 1;
                        mem_dout <= cache[cache_addr[11:2]][15:8];
                    end
                    2: begin
                        mem_a <= {cache[cache_addr[11:2]][47:32],2'b10};
                        mem_wr <= 1;
                        mem_dout <= cache[cache_addr[11:2]][23:16];
                    end
                    3: begin
                        mem_a <= {cache[cache_addr[11:2]][47:32],2'b11};
                        mem_wr <= 1;
                        mem_dout <= cache[cache_addr[11:2]][31:24];
                        cache[cache_addr[11:2]] <= 0;
                    end
                endcase
                cache_ready <= cache_ready +1;
            end
            else if (cache_ready == 0)begin
                cache_ready <= 4;
            end
            else begin
                case (cache_ready)
                    4:begin
                        mem_a <={cache_addr[17:2],2'b00};
                        mem_wr <= 0;
                    end 
                    5:begin
                        mem_a <={cache_addr[17:2],2'b01};
                        mem_wr <= 0;
                    end
                    6:begin
                        cache[cache_addr[11:2]][7:0] <= mem_din;
                        mem_a <={cache_addr[17:2],2'b10};
                        mem_wr <= 0;
                    end
                    7:begin
                        cache[cache_addr[11:2]][15:8] <= mem_din;
                        mem_a <={cache_addr[17:2],2'b11};
                        mem_wr <= 0;
                    end
                    8:begin
                        cache[cache_addr[11:2]][23:16] <= mem_din;
                    end
                    9:begin
                        cache[cache_addr[11:2]][48:24] <={1'b1,cache_addr[17:2],mem_din};                        
                    end
                endcase
                cache_ready <= cache_ready +1;
            end
        end
        else begin
            if ((cache_io)) begin
                case (cache_ready)
                    0:begin
                        mem_a <= cache_addr[17:0];
                        mem_dout <= cache_out[7:0];
                        mem_wr <= 1;
                        if (cache_len ==1) cache_ready <= 31;else
                        cache_ready <= 1;
                    end
                    1:begin
                        mem_a <= cache_addr[17:0]+1;
                        mem_dout <= cache_out[15:8];
                        mem_wr <= 1;
                        if (cache_len ==2) cache_ready <= 31;else
                        cache_ready <= 2;
                    end
                    2:begin
                        //$display("output");
                        mem_a <= cache_addr[17:0]+2;
                        mem_dout <= cache_out[23:16];
                        mem_wr <= 1;
                        cache_ready <= 3;
                    end
                    3:begin
                        mem_a <= cache_addr[17:0]+3;
                        mem_dout <= cache_out[31:24];
                        mem_wr <= 1;
                        cache_ready <=31;
                    end
                endcase
            end
            else if ((!cache_io))begin
                case (cache_ready)
                    0:begin
                        mem_a <= cache_addr[17:0];
                        mem_wr <= 0;
                        cache_ready <= 1;
                    end
                    1:begin
                        cache_ready <= 2;
                    end
                    2:begin
                        cache_in[7:0] <= mem_din;
                        if (cache_len == 1)cache_ready <= 31;
                        else cache_ready <= 3;
                    end
                    3:begin
                        mem_a <= cache_addr[17:0]+1;
                        mem_wr <= 0;
                        cache_ready <= 4;
                    end
                    4:begin
                        cache_ready <= 5;
                    end
                    5:begin
                        cache_in[15:8] <= mem_din;
                        if (cache_len == 2) cache_ready <= 31;
                        else cache_ready <= 6;
                    end
                    6:begin
                        mem_a <= cache_addr[17:0]+2;
                        mem_wr <= 0;
                        cache_ready <= 7;
                    end
                    7: cache_ready <= 8;
                    8:begin
                        cache_in[23:16] <= mem_din;
                        cache_ready <= 9;
                    end
                    9:begin
                        mem_a <= cache_addr[17:0]+3;
                        mem_wr <= 0;
                        cache_ready <= 10;
                    end
                    10: cache_ready <= 11;
                    11:begin
                        cache_in[31:24] <= mem_din;
                        cache_ready <= 31;
                    end
                endcase
            end
        end 
    end 
    else if (cache_ready == 31) begin
        mem_wr <=0;
        mem_a <= 0;
    end
    ////
	end
	always @(posedge clk_in) begin
		reg0[0] <= 0;
		if (rst_in)
		  begin
			cache_busy <= 0;
			cache_ready <= 0;
			//cache_in <= 0;
			instr_cnt <= 0;
			instr_1 <= 0;
			instr_2 <= 0;
			//dbgreg_dout <= 0;
			regpc <= 0;
			pipe1_ready <= 0;
			pipe2_ready <= 0;
			pipe3_ready <= 0;
			alu_opcode <= 0;
			mem_wr <= 0;
			mem_a <= 0;
			mem_dout <= 0;
			tot_instr <= 0;
			for (k=0;k<32;k=k+1) begin
			    reg0[k] <= 0;
			end
		  end
		else if (!rdy_in)
		  begin
			
			
		  end
		else
		  begin
			if ((pipe1_ready == 0)&&(instr_cnt == 0)&&(instr_1[32] == 0)) begin
               
                  pipe1_ready <= 1;
                  instr_cnt <= 1;
                  cache_busy <= 1;
                  cache_ready <= 0;
                  cache_io <= 0;
                  cache_addr <= regpc[17:0];
                  cache_len <= 4;
              
              regpc <= regpc + 4;
              tot_instr <= tot_instr +1;
          end 
          else if (pipe1_ready ==1) begin
              if (cache_ready == 31) begin
                  instr_1[31:0] <= cache_in;
                  instr_1[32] <= 1;
                  pipe1_ready <= 0;//reset the pipe1
                  cache_busy <= 0;
                  cache_ready <= 0;
              end
          end
          //this is the second pipeline
          if ((pipe2_ready == 0)&&(instr_1[32]==1)&&(instr_2[32]==0)) begin
              case (instr_1[6:0])
                  //LUI
                  7'b0110111: begin
                      instr_2 <= instr_1;
                      reg0[ instr_1[11:7] ] <= { instr_1[31:12] ,12'b000000000000 };
                      instr_1 <= 0;
                      //pipe2_ready <= 0;
                  end 
                  //AUIPC
                  7'b0010111: begin
                      instr_2 <= instr_1;
                      val1 <= regpc -4  + { instr_1[31:12] ,12'b000000000000 };
                      instr_1 <= 0;
                  end 
                  //JAL
                  7'b1101111: begin
                      instr_2 <= instr_1;
                      reg0[instr_1[11:7]] <= regpc;
                      val1[31:20] <= {12{instr_1[31]}};
                      val1[19:12] <= instr_1[19:12];
                      val1[11] <= instr_1[20];
                      val1[10:1] <= instr_1[30:21];
                      val1[0] <= 0;
                      instr_1 <= 0;
                  end
                  //JALR
                  7'b1100111: begin
                      instr_2 <= instr_1;
                      reg0[instr_1[11:7]] <= regpc;
                      instr_1 <= 0;
                  end 
                  //BEQ BNE BLT[U] BGE[U]
                  7'b1100011: begin
                      //common command
                      instr_2 <= instr_1;
                      val3[31:12] <= {20{instr_1[31]}};
                      val3[11] <= instr_1[7];
                      val3[10:5] <= instr_1[30:25];
                      val3[4:1] <= instr_1[11:8];
                      val3[0] <= 0;
                      val1 <= reg0[instr_1[19:15]];
                      val2 <= reg0[instr_1[24:20]];
                      instr_1 <= 0;
                  end
                  //load
                  7'b0000011: begin
                      instr_2 <= instr_1;
                      val3 <= reg0[instr_1[19:15]] + {{20{instr_1[31]}},instr_1[31:20]};
                      instr_1 <= 0;
                  end
                  //store
                  7'b0100011: begin
                      instr_2 <= instr_1;
                      val3 <= reg0[instr_1[19:15]] + {{20{instr_1[31]}},instr_1[31:25],instr_1[11:7]};
                      instr_1 <= 0;
                  end
                  //alu1
                  7'b0010011: begin
                      instr_2 <= instr_1;
                      val1 <= reg0[instr_1[19:15]];
                      val2 <= {{20{instr_1[31]}},instr_1[31:20]};
                      case (instr_1[14:12])
                          //addi
                          3'b000:begin
                              alu_opcode <= 1;
                          end
                          //slti
                          3'b010:begin
                              alu_opcode <= 12;
                          end
                          //sltiu
                          3'b011:begin
                              alu_opcode <= 11;
                          end
                          //xori
                          3'b100:begin
                              alu_opcode <= 8;
                          end
                          //ori
                          3'b110:begin
                              alu_opcode <= 7;
                          end
                          //andi
                          3'b111:begin
                              alu_opcode <= 6;
                          end
                          //slli
                          3'b001:begin
                              alu_opcode <= 3;
                          end
                          //sr
                          3'b101:begin
                              //srl
                              if (instr_1[30] == 0) begin
                                  alu_opcode <= 4;
                              end 
                              //sra
                              else begin
                                  alu_opcode <= 5;
                              end
                          end
                      endcase
                      instr_1 <= 0;
                  end
                  //alu2
                  7'b0110011: begin
                      instr_2 <= instr_1;
                      val1 <= reg0[instr_1[19:15]];
                      val2 <= reg0[instr_1[24:20]];
                      instr_1 <= 0;
                      case (instr_1[14:12])
                          3'b000: begin
                              //sub
                              if (instr_1[30]) begin
                                  alu_opcode <= 2;
                              end 
                              //add
                              else begin
                                  alu_opcode <= 1;
                              end
                          end
                          //sll
                          3'b001: alu_opcode <= 3;
                          //slt
                          3'b010: alu_opcode <= 12;
                          //sltu
                          3'b011: alu_opcode <= 11;
                          //xor
                          3'b100: alu_opcode <= 8;
                          //sr
                          3'b101: begin
                              //sra
                              if (instr_1[30]) begin
                                  alu_opcode <= 5;
                              end 
                              //srl
                              else begin
                                  alu_opcode <= 4;
                              end
                          end
                          //or
                          3'b110: alu_opcode <= 7;
                          //and
                          3'b111: alu_opcode <= 6;
                      endcase
                  end
              endcase
          end 
          
          //this is the third pipeline
          if ((pipe3_ready == 0)&& (instr_2[32]==1)) begin
              case (instr_2[6:0])
                  7'b0110111: begin
                      instr_2 <= 0;
                      instr_cnt <= 0;
                  end
                  7'b0010111: begin
                      reg0[ instr_2[11:7] ] <= val1;
                      instr_2 <= 0;
                      val1 <= 0;
                      instr_cnt <= 0;
                  end 
                  7'b1101111: begin
                      regpc <= regpc -4 + val1;
                      instr_2 <= 0;
                      instr_cnt <= 0;
                      val1 <= 0;
                  end
                  7'b1100111: begin
                      regpc <= reg0[instr_2[19:15]] + instr_2[31:20];
                      instr_2 <= 0;
                      instr_cnt <= 0;
                  end 
                  7'b1100011: begin
                      case (instr_2[14:12])
                          //BEQ
                          3'b000:begin
                              if (val1 == val2) begin
                                  regpc <= regpc -4 + val3;
                              end
                          end
                          //BNE
                          3'b001:begin
                              if (val1 != val2) begin
                                  regpc <= regpc -4 + val3;
                              end
                          end
                          //BLT
                          3'b100:begin
                              if ((val1^{1'b1,{31{1'b0}}})< (val2^{1'b1,{31{1'b0}}})) begin
                                  regpc <= regpc -4 + val3;
                              end
                          end
                          //BGE
                          3'b101:begin
                              if ((val1^{1'b1,{31{1'b0}}}) >= (val2^{1'b1,{31{1'b0}}})) begin
                                  regpc <= regpc -4 + val3;
                              end
                          end
                          //BLTU
                          3'b110:begin
                              if (val1 < val2) begin
                                  regpc <= regpc -4 + val3;
                              end
                          end
                          //BGEU
                          3'b111:begin
                              if (val1 >= val2) begin
                                  regpc <= regpc -4 + val3;
                              end
                          end
                      endcase
                      instr_2 <= 0;
                      instr_cnt <= 0;
                  end
                  7'b0000011: begin
                      pipe3_ready <= 1;
                      cache_ready <= 0;
                      cache_busy <= 3;
                      cache_io <= 0;
                      cache_addr <= val3[17:0];
                      case (instr_2[14:12])
                          //LB LH LW LBU LHU
                          3'b000:cache_len <= 1;
                          3'b001:cache_len <= 2;
                          3'b010:cache_len <= 4;
                          3'b100:cache_len <= 1;
                          3'b101:cache_len <= 2;
                      endcase
                  end
                  7'b0100011: begin
                      pipe3_ready <= 1;
                      cache_ready <= 0;
                      cache_busy <= 3;
                      cache_io <= 1;
                      cache_addr <= val3[17:0];
                      cache_out <= reg0[instr_2[24:20]];
                      case (instr_2[14:12])
                          //SB SH SW
                          3'b000:cache_len <= 1;
                          3'b001:cache_len <= 2;
                          3'b010:cache_len <= 4;
                      endcase
                  end
                  7'b0010011: begin
                      reg0[instr_2[11:7]] <= res1;
                      instr_cnt <= 0;
                      instr_2 <= 0;
                  end
                  7'b0110011: begin
                      reg0[instr_2[11:7]] <= res1;
                      instr_cnt <= 0;
                      instr_2 <= 0;
                  end
              endcase
          end
          else if (pipe3_ready != 0) begin
              case (instr_2[6:0])
                  7'b0000011: if (cache_ready == 31) begin
                      cache_ready <= 0;
                      cache_busy <= 0;
                      cache_addr <= 0;
                      case (instr_2[14:12])
                          3'b000:begin
                              reg0[instr_2[11:7]] <= {{24{cache_in[7]}},cache_in[7:0]};
                          end
                          3'b001:begin
                              reg0[instr_2[11:7]] <= {{16{cache_in[15]}},cache_in[15:0]};
                          end
                          3'b010:begin
                              reg0[instr_2[11:7]] <= cache_in[31:0];
                          end
                          3'b100:begin
                              reg0[instr_2[11:7]] <= {{24{1'b0}},cache_in[7:0]};
                          end
                          3'b101:begin
                              reg0[instr_2[11:7]] <= {{16{1'b0}},cache_in[15:0]};
                          end
                      endcase
                      pipe3_ready <= 0;
                      instr_cnt <= 0;
                      instr_2 <= 0;
                  end
                  7'b0100011: if (cache_ready == 31) begin
                      cache_ready <= 0;
                      cache_busy <= 0;
                      cache_addr <= 0;
                      pipe3_ready <= 0;
                      instr_2 <= 0;
                      instr_cnt <= 0;
                  end
              endcase
          end
        end
    end

endmodule



