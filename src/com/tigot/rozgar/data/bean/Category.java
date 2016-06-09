package com.tigot.rozgar.data.bean;

import java.util.Date;

public class Category {

	private Integer id;
	private Integer categoryId;
	private String title;
	private String image;
	private Date lastDownloaded;
	public Category() {
		super();
	}
	public Category(Integer id, Integer categoryId, String title, String image, Date lastDownloaded) {
		super();
		this.id = id;
		this.categoryId = categoryId;
		this.title = title;
		this.image = image;
		this.lastDownloaded = lastDownloaded;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Date getLastDownloaded() {
		return lastDownloaded;
	}
	public void setLastDownloaded(Date lastDownloaded) {
		this.lastDownloaded = lastDownloaded;
	}
	
}
