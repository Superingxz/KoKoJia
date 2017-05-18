package com.nuoxian.kokojia.view.expandgridview.model;


/**
 *
 * 类: DargChildInfo <p>
 * 描述: 子item显示 <p>
 * 作者: wedcel wedcel@gmail.com<p>
 * 时间: 2015年8月25日 下午5:24:04 <p>
 */
public class DargChildInfo {

	private int id;
	private String classId;
	private String name;
	private String CourseName;
	private String className;//用作在小标题中保存大标题的名称

	public DargChildInfo() {
		// TODO Auto-generated constructor stub
	}


	public DargChildInfo(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCourseName() {
		return CourseName;
	}

	public void setCourseName(String courseName) {
		CourseName = courseName;
	}
}
