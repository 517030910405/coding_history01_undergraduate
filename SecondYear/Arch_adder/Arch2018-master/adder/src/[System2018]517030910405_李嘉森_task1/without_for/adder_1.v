/* ACM Class System (I) 2018 Fall Assignment 1 
 *
 * Part I: Write an adder in Verilog
 *
 * Implement your naive adder here
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
	wire	c[15:0];
	AddHalf	M1	(c[0], s[0], a[0], b[0]);
	AddFull	M2	(c[1], s[1], a[1], b[1], c[0]);
	AddFull	M3	(c[2], s[2], a[2], b[2], c[1]);
	AddFull	M4	(c[3], s[3], a[3], b[3], c[2]);
	AddFull	M5	(c[4], s[4], a[4], b[4], c[3]);
	AddFull	M6	(c[5], s[5], a[5], b[5], c[4]);
	AddFull	M7	(c[6], s[6], a[6], b[6], c[5]);
	AddFull	M8	(c[7], s[7], a[7], b[7], c[6]);
	AddFull	M9	(c[8], s[8], a[8], b[8], c[7]);
	AddFull	M10	(c[9], s[9], a[9], b[9], c[8]);
	AddFull	M11	(c[10], s[10], a[10], b[10], c[9]);
	AddFull	M12	(c[11], s[11], a[11], b[11], c[10]);
	AddFull	M13	(c[12], s[12], a[12], b[12], c[11]);
	AddFull	M14	(c[13], s[13], a[13], b[13], c[12]);
	AddFull	M15	(c[14], s[14], a[14], b[14], c[13]);
	AddFull	M16	(c1, s[15], a[15], b[15], c[14]);
endmodule
//Add two signals
module AddHalf(output c, s, input a, b);
	xor	M1	(s, a, b);
	and	M2	(c, a, b);
endmodule

//Add three signals
module AddFull(output c1, s, input a, b, c0);
	wire 		w1,w2,w3;
	AddHalf	M1	(w1, w2, a, b);
	AddHalf	M2	(w3, s, c0, w2);
	or		M3	(c1, w1, w3);
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
	// Hint: You can use generate statement in Verilog to create multiple instantiations of modules and code.
