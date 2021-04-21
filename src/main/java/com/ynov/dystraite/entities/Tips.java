package com.ynov.dystraite.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tips {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "content", nullable = false)
	private String content;
	
	@Column(name = "created_at")
	private Date createdAt;	
	
	@ManyToOne
    @JoinColumn(name="owner_id")
	@JsonIncludeProperties({"username", "profilePicture"})
    private Users owner;
	
	@Column(name = "likes")
	@ManyToMany()
	@JsonIgnore
	@JoinTable(
			  name = "users_tips", 
			  joinColumns = @JoinColumn(name = "id"), 
			  inverseJoinColumns = @JoinColumn(name = "email"))
	private List<Users> likes;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@Column(name = "tags")
    @ElementCollection(targetClass=String.class)
	private List<String> tags;
	
	@Formula("(SELECT COUNT(i.id) FROM users_tips i WHERE id = i.id)")
    private long nbLikes;
	

}
