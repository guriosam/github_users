package objects;

public class CommitDAO {

	private String author;
	private String hash;
	private String date;
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public boolean check(){
		if(author != null){
			if(!author.equals("")){
				if(hash != null){
					if(!hash.equals("")){
						if(date != null){
							if(!date.equals("")){
								return true;
							}
						}
					}
				}
			}
		}
		
		return false;
	}
	
	
	
}
