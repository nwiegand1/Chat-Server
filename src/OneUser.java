
public class OneUser implements Comparable <OneUser>{

	private int idNum;
	private String nickname;
	
	OneUser (int idNum, String nickname) {
		this.idNum = idNum;
		this.nickname = nickname;
	}
	
	public int getId () {
		return idNum;
	}
	
	public String getNickname () {
		return nickname;
	}
	
	public void setNickname (String newNickname) {
		this.nickname = newNickname;
	}
	@Override
	public boolean equals (Object other) {
		if (other instanceof OneUser)
		{
		return (this.idNum == ((OneUser) other).idNum);
		}
		return false;
	}
	@Override
	public int compareTo(OneUser other) {
		return Double.compare(this.idNum, other.idNum);
	}
		
	@Override
	public String toString ()
	{
		return nickname;
	}
}
