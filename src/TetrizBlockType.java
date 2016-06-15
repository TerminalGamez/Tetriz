
public enum TetrizBlockType {
	sqää1(0), sqää2(0), sqää3(0), sqää4(0),
	L1(1), L2(1), L3(1), L4(1),
	LKomp1(2), LKomp2(2), LKomp3(2), LKomp4(2),
	I1(3), I2(3), I3(3), I4(3),
	S1(4), S2(4), S3(4), S4(4),
	Skomp1(5), Skomp2(5), Skomp3(5), Skomp4(5),
	T1(6), T2(6), T3(6), T4(6);
	
	public int number;
	TetrizBlockType(int number){
		this.number = number;
	}
}
