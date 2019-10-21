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
	begin
		genvar i;
		for (i=1;i<=14;i=i+1)
		begin
			AddFull	M2 (c[i], s[i], a[i], b[i], c[i-1]);
		end
	end
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
	//	 the first 2 ports input two 16-bit unsigned numbers as the addends
	//	 the third port outputs a 16-bit unsigned number as the sum
	//	   the forth port outputs a 1-bit carry flag as the overflow
	// 

	// TODO: Implement this module here
	// Hint: You can use generate statement in Verilog to create multiple instantiations of modules and code.
