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
	wire [15:0] g = a&b;
	wire [15:0] c;
	assign s[0] = p[0];
	assign c[0] = g[0];
	begin
		genvar i, j, k;
		for (i=1; i<=15; i=i+1)
		begin
			wire [i:0] pl,cl;
			assign pl[i] = p[i];
			for (j=i-1; j>=0; j=j-1)
			begin
				assign pl[j]=pl[j+1]&p[j];
			end
			assign cl[i] = pl[i]&g[i-1];
			for (k=i-1; k>=1; k=k-1)
			begin
				assign cl[k]=cl[k+1]|(pl[k]&g[k-1]);
			end
			assign cl[0] = 0;
			assign c[i] = cl[1] + g[i];
			assign s[i] = p[i] ^ c[i-1];
		end
	end
	assign c1 = c[15];
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
