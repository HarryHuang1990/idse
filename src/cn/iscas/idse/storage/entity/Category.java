package cn.iscas.idse.storage.entity;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;
import static com.sleepycat.persist.model.Relationship.*;
import static com.sleepycat.persist.model.DeleteAction.*;

/**
 * <p>
 * an Entity used to represent the category of file type
 * </p>
 * <p>structure:</p> 
 * <ol>
 * 	<li>category id</li>
 * 	<li>category name (video, audio, image, text, executable, code, compress)</li>
 * </ol>
 * @author Harry Huang
 *
 */
@Entity
public class Category {
	
	@PrimaryKey(sequence="categoryID")
	private byte categoryID;
	
	@SecondaryKey(relate=ONE_TO_ONE)
	private String categoryName;

	private Category(){}
	
	public Category(String categoryName){
		this.categoryName = categoryName;
	}
	
	public Category(byte categoryID, String categoryName){
		this.categoryID = categoryID;
		this.categoryName = categoryName;
	}
	
	public byte getCategoryID() {
		return categoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}
}
