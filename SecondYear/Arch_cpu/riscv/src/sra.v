module sra(
	input [31:0] a,
	input [31:0] b,
	output reg [31:0] c
);
	reg [31:0] f0,f1,f2,f3,f4;
	always@(a or b or f0 or f1 or f2 or f3 or f4)
		begin
			if (|(b[31:5])) begin
				c[31:0] <= {32{a[31]}};
			end else begin
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
		end
endmodule