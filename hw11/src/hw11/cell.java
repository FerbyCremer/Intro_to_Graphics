package hw11;

class cell {
	private int status;
	protected boolean dead;
	public cell(){
		this.dead = false;
		this.status = 0xFF000000;
	}
	int getColor(){
		return this.status;
	}
	void setColor(int color){
		this.status = color;
	}
	boolean isDead(){
		if(this.status == 0xFF000000 || this.status == 0xFFFF0000)
			dead = true;
		else
			dead = false;
		return dead;
	}
}
