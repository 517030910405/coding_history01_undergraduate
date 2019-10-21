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
	assign s[1] = p[1] ^ c[0];
	assign c[1] = g[1] |(c[0] & p[1]);
	assign s[2] = p[2] ^ c[1];
	assign c[2] = g[2] |(c[1] & p[2]);
	assign s[3] = p[3] ^ c[2];
	assign c[3] = g[3] |(c[2] & p[3]);
	assign s[4] = p[4] ^ c[3];
	assign c[4] = g[4] |(c[3] & p[4]);
	assign s[5] = p[5] ^ c[4];
	assign c[5] = g[5] |(c[4] & p[5]);
	assign s[6] = p[6] ^ c[5];
	assign c[6] = g[6] |(c[5] & p[6]);
	assign s[7] = p[7] ^ c[6];
	assign c[7] = g[7] |(c[6] & p[7]);
	assign s[8] = p[8] ^ c[7];
	assign c[8] = g[8] |(c[7] & p[8]);
	assign s[9] = p[9] ^ c[8];
	assign c[9] = g[9] |(c[8] & p[9]);
	assign s[10] = p[10] ^ c[9];
	assign c[10] = g[10] |(c[9] & p[10]);
	assign s[11] = p[11] ^ c[10];
	assign c[11] = g[11] |(c[10] & p[11]);
	assign s[12] = p[12] ^ c[11];
	assign c[12] = g[12] |(c[11] & p[12]);
	assign s[13] = p[13] ^ c[12];
	assign c[13] = g[13] |(c[12] & p[13]);
	assign s[14] = p[14] ^ c[13];
	assign c[14] = g[14] |(c[13] & p[14]);
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
