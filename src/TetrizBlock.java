
public class TetrizBlock {

	private int rotation;
	public String[][] string;
	private TetrizBlockType type;
    public final static int width = 12;
    public final static int height = 8;
	
	
	public TetrizBlock(TetrizBlockType type){
		this.string = TetrizBlockStrings.blockString.get(type);
		this.type = type;
	}
	
	public TetrizBlock(TetrizBlock copy){
		this(copy.type);
	}
	
	
	public void rotate(boolean clockwise){
		//Change rotate var
		if(clockwise){
			rotation = (rotation+1)%4;
		} else {
			rotation--;
			if(rotation < 0) rotation = 3;
		}
		//Set new type according to rotation
		type = setNewType();
		
		//Set new string according to type
		string = TetrizBlockStrings.blockString.get(type);
	}

	private TetrizBlockType setNewType(){
		switch (type.number) {
		case 0:
			switch(rotation){
				case 0: return TetrizBlockType.sqää1;
				case 1: return TetrizBlockType.sqää2;
				case 2: return TetrizBlockType.sqää3;
				case 3: return TetrizBlockType.sqää4;
			}
		case 1:
			switch(rotation){
				case 0: return TetrizBlockType.L1;
				case 1: return TetrizBlockType.L2;
				case 2: return TetrizBlockType.L3;
				case 3: return TetrizBlockType.L4;
			}
		case 2:
			switch(rotation){
				case 0: return TetrizBlockType.LKomp1;
				case 1: return TetrizBlockType.LKomp2;
				case 2: return TetrizBlockType.LKomp3;
				case 3: return TetrizBlockType.LKomp4;
			}
		case 3:
			switch(rotation){
				case 0: return TetrizBlockType.I1;
				case 1: return TetrizBlockType.I2;
				case 2: return TetrizBlockType.I3;
				case 3: return TetrizBlockType.I4;
			}
		case 4:
			switch(rotation){
				case 0: return TetrizBlockType.S1;
				case 1: return TetrizBlockType.S2;
				case 2: return TetrizBlockType.S3;
				case 3: return TetrizBlockType.S4;
			}
		case 5:
			switch(rotation){
				case 0: return TetrizBlockType.Skomp1;
				case 1: return TetrizBlockType.Skomp2;
				case 2: return TetrizBlockType.Skomp3;
				case 3: return TetrizBlockType.Skomp4;
			}
		case 6:
			switch(rotation){
				case 0: return TetrizBlockType.T1;
				case 1: return TetrizBlockType.T2;
				case 2: return TetrizBlockType.T3;
				case 3: return TetrizBlockType.T4;
			}
			default:
				return TetrizBlockType.sqää1;
		}
	}
	
	public static TetrizBlock generateNewBlock(int randomInt){
		switch (randomInt) {
		case 0:
			return new TetrizBlock(TetrizBlockType.sqää1);
		case 1:
			return new TetrizBlock(TetrizBlockType.L1);
		case 2:
			return new TetrizBlock(TetrizBlockType.LKomp1);
		case 3:
			return new TetrizBlock(TetrizBlockType.I1);
		case 4:
			return new TetrizBlock(TetrizBlockType.S1);
		case 5:
			return new TetrizBlock(TetrizBlockType.Skomp1);
		case 6:
			return new TetrizBlock(TetrizBlockType.T1);
		default:
			return new TetrizBlock(TetrizBlockType.sqää1);
		}
	}
	
	public static int max(){
		return TetrizBlockType.values().length;
	}
	
	
}
