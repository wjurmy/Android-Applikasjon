package com.tigot.rozgar.data.bean;

import java.util.Date;

import com.tigot.rozgar.utils.PrimitiveUtils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * NewsItem implements Parcelable to allow saving current newsItem object in NewItemFragment state.
 */
public class NewsItem implements Parcelable {

	private Integer id;
	private String title;
	private String author;
	private Integer categoryId;
	private String articleImage;
	private Date created;
	private String abstractTitle;
	private String imageCaption;
	private String body;
	private String idMD5;

	public NewsItem() {
		super();
	}
	public NewsItem(String title, String author, Integer categoryId, String articleImage, Date created, String abstractTitle, String imageCaption, String body) {
		this(null, title, author, categoryId, articleImage, created, abstractTitle, imageCaption, body, PrimitiveUtils.md5(title + created));
	}
	public NewsItem(Integer id, String title, String author, Integer categoryId, String articleImage, Date created, String abstractTitle, String imageCaption, String body, /*String articleImageMD5, String imageCaptionMD5, */String idMD5) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.categoryId = categoryId;
		this.articleImage = articleImage;
		this.created = created;
		this.abstractTitle = abstractTitle;
		this.imageCaption = imageCaption;
		this.body = body;
		this.idMD5 = idMD5;
	}
	private NewsItem(Parcel in) {
		id = in.readInt();
		title = in.readString();
		author = in.readString();
		categoryId = in.readInt();
		articleImage = in.readString();
		created = new Date(in.readLong());
		abstractTitle = in.readString();
		imageCaption = in.readString();
		body = in.readString();
		idMD5 = in.readString();
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public String getArticleImage() {
		return articleImage;
	}
	public void setArticleImage(String articleImage) {
		this.articleImage = articleImage;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getAbstractTitle() {
		return abstractTitle;
	}
	public void setAbstractTitle(String abstractTitle) {
		this.abstractTitle = abstractTitle;
	}
	public String getImageCaption() {
		return imageCaption;
	}
	public void setImageCaption(String imageCaption) {
		this.imageCaption = imageCaption;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getIdMD5() {
		return idMD5;
	}
	public void setIdMD5(String idMD5) {
		this.idMD5 = idMD5;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeString(author);
		dest.writeInt(categoryId);
		dest.writeString(articleImage);
		dest.writeLong(created.getTime());
		dest.writeString(imageCaption);
		dest.writeString(body);
		dest.writeString(idMD5);
	}
	public static final Parcelable.Creator<NewsItem> CREATOR = new Parcelable.Creator<NewsItem>() {
		@Override
		public NewsItem createFromParcel(Parcel source) {
			return new NewsItem(source);
		}
		@Override
		public NewsItem[] newArray(int size) {
			return new NewsItem[size];
		}
	};
	@Override
	public String toString() {
		return "NewsItem [id=" + id + ", title=" + title + ", author=" + author + ", categoryId=" + categoryId
				+ ", articleImage=" + articleImage + ", created=" + created
				+ ", abstractTitle=" + abstractTitle + ", imageCaption=" + imageCaption 
				+ ", body=" + (body.length() > 30 ? body.substring(0, 30) : body) + ", idMD5=" + idMD5 + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idMD5 == null) ? 0 : idMD5.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NewsItem other = (NewsItem) obj;
		if (idMD5 == null) {
			if (other.idMD5 != null)
				return false;
		} else if (!idMD5.equals(other.idMD5))
			return false;
		return true;
	}
}
