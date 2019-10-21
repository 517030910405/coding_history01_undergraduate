module alu(
	input	wire	[6:0]	opcode,
	input	wire	[31:0]	a,
	input	wire	[31:0]	b,
	output	reg		[31:0]	c
	
);
    wire [31:0] sra_op;
    sra sra(a,b,sra_op);
	always@(opcode or a or b or sra_op) begin
		case(opcode)
			1:c <= (a + b);//add
			2:c <= (a - b);//sub
			3:c <= (a << b[4:0]);//sll
			4:c <= (a >> b[4:0]);//srl
			5:c <= sra_op;//sra
			6:c <= (a & b);//and
			7:c <= (a | b);//or
			8:c <= (a ^ b);//xor
			9:c <= (a==b);//BEQ be equal
			10:c <= (a!=b);//BNE be not equal
			11:c <= (a < b);//SLTU
			12:c <= ({a[31]^1'b1,a[30:0]} < {b[31]^1'b1,b[30:0]});//SLT
			//default:;
		endcase
	end
endmodule
module sra(
	input [31:0] a,
	input [31:0] b,
	output reg [31:0] c
);
	reg [31:0] f0,f1,f2,f3,f4;
	always@(a or b or f0 or f1 or f2 or f3 or f4)
		begin
			if (b[0]) begin
				f0[30:0] <= a[31:1];
				f0[31:31] <= a[31:31];
			end else begin
				f0[31:0] <= a[31:0];
			end
			if (b[1]) begin
				f1[29:0] <= f0[31:2];
				f1[31:30] <= {2{a[31]}};
			end else begin
				f1[31:0] <= f0[31:0];
			end
			if (b[2]) begin
				f2[27:0] <= f1[31:4];
				f2[31:28] <= {4{a[31]}};
			end else begin
				f2[31:0] <= f1[31:0];
			end
			if (b[3]) begin
				f3[23:0] <= f2[31:8];
				f3[31:24] <= {8{a[31]}};
			end else begin
				f3[31:0] <= f2[31:0];
			end
			if (b[4]) begin
				f4[15:0] <= f3[31:16];
				f4[31:16] <= {16{a[31]}};
			end else begin
				f4[31:0] <= f3[31:0];
			end
			c <= f4;	
		end
endmodule
/*
module sra(
	input [31:0] a,
	input [31:0] b,
	output reg [31:0] c
);
	reg [31:0] f0,f1,f2,f3,f4;
	always@(a or b)
		begin
			if (|(b[31:5])) begin
				c[31:0] = {32{a[31]}};
			end else begin
				if (b[0]) begin
					f0[30:0] = a[31:1];
					f0[31:31] = a[31:31];
				end else begin
					f0[31:0] = a[31:0];
				end
				if (b[1]) begin
					f1[29:0] = f0[31:2];
					f1[31:30] = {2{a[31]}};
				end else begin
					f1[31:0] = f0[31:0];
				end
				if (b[2]) begin
					f2[27:0] = f1[31:4];
					f2[31:28] = {4{a[31]}};
				end else begin
					f2[31:0] = f1[31:0];
				end
				if (b[3]) begin
					f3[23:0] = f2[31:8];
					f3[31:24] = {8{a[31]}};
				end else begin
					f3[31:0] = f2[31:0];
				end
				if (b[4]) begin
					f4[15:0] = f3[31:16];
					f4[31:16] = {16{a[31]}};
				end else begin
					f4[31:0] = f3[31:0];
				end
				c = f4;
			end
		end
endmodule*/