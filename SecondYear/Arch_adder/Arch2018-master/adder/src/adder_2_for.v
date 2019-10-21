/* ACM Class System (I) 2018 Fall Assignment 1 
 *
 * Part I: Write an adder in Verilog
 *
 * Implement your Carry Look Ahead Adder here
 * 
 * GUIDE:
 *   1. Create a RTL project in Vivado
 *   2. Put this file into `Sources'
 *   3. Put `test_adder.v' into `Simulation Sources'
 *   4. Run Behavioral Simulation
 *   5. Make sure to run at least 100 steps during the simulation (usually 100ns)
 *   6. You can see the results in `Tcl console'
 *
 */

module adder(input [15:0] a, b, output [15:0] s, output c1);
	wire [15:0] p = a^b;
	//p is the previous s
	wire [15:0] g = a&b;
	//g is the previous c
	wire [15:0] c;
	assign s[0] = p[0];
	assign c[0] = g[0];
	begin
		genvar i;
		for (i=1; i<=14; i=i+1)
		begin
			assign s[i] = p[i] ^ c[i-1];
			assign c[i] = g[i] |(c[i-1] & p[i]);
		end
	end
	assign s[15] = p[15] ^ c[14];
	assign c1 = g[15] |(c[14] & p[15]);
endmodule

	// TODO: Write the ports of this module here
	//
	// Hint: 
	//   The module needs 4 ports, 
	//     the first 2 ports input two 16-bit unsigned numbers as the addends
	//     the third port outputs a 16-bit unsigned number as the sum
	//	   the forth port outputs a 1-bit carry flag as the overflow
	// 
	// TODO: Implement this module here
