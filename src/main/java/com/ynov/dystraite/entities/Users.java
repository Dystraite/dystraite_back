package com.ynov.dystraite.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ynov.dystraite.entities.maximots.UserGrid;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Users implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "lastname")
	private String lastname;
	
	@Column(name = "firstname")
	private String firstname;

	@Column(name = "username")
	private String username;

	@Column(name = "birthdate")
	private Date birthdate;
	
	@Column(name = "latitude")
	private Long latitude;
	
	@Column(name = "longitude")
	private Long longitude;

	@Column(name = "city")
	private String city;
	
	@Column(name = "zip_code")
	private Integer zipCode;
	
	@Column(name = "password", nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	
	@Column(name = "role")
	private String role;
	
	@Lob
	@Column(name= "profile_picture", columnDefinition="MEDIUMBLOB", length = 20971520)
	private String profilePicture;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@Column(name = "liked")
	@ManyToMany(mappedBy = "likes")
	private List<Tips> likedTips;
	
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(mappedBy="owner")
    private List<Tips> tips;
	
	@OneToOne
	@JoinColumn(name = "speech_therapist", referencedColumnName = "email")
	private Users speechTherapist;

	@LazyCollection(LazyCollectionOption.FALSE)
	@JsonIgnore
	@OneToMany(mappedBy = "user")
	@Column(name = "user_grid_id")
	private List<UserGrid> userGrids;

	public Users(String email, String lastname, String firstname, String username, Date birthdate, long latitude, long longitude,
			String city, int zipCode, String password, String role, String profilePicture, Users speechTherapist) {
		super();
		this.email = email;
		this.lastname = lastname;
		this.firstname = firstname;
		this.username = username;
		this.birthdate = birthdate;
		this.latitude = latitude;
		this.longitude = longitude;
		this.city = city;
		this.zipCode = zipCode;
		this.password = password;
		this.role = role;
		this.profilePicture = profilePicture;
		this.speechTherapist = speechTherapist;
	}

	//@Override
	//public String toString() {
	//	return "Users [email=" + email + ", lastname=" + lastname + ", firstname=" + firstname + ", birthdate="
	//			+ birthdate + ", latitude=" + latitude + ", longitude=" + longitude + ", city=" + city
	//			+ ", zip_code=" + zipCode + ", password=" + password + ", role=" + role + ", profile_picture=" + profilePicture
	//			+ ", speech_therapist=" + speechTherapist + "]";
	//}

	
}
