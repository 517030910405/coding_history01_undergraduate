module cache(
	input wire clk;
	input wire [31:0] addr;
	input wire op_code;
	input wire [31:0] data_in;
	output wire [31:0] data_out;
);
	always@(addr or op_code or data_in) begin
	
	
	end
	
	

endmodule