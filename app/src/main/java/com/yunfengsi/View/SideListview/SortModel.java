package com.yunfengsi.View.SideListview;

public class SortModel {

	private String name;   //��ʾ����ϵ������
	private String sortLetters;  //��ʾ����ƴ��������ĸ
	private String number;//�绰����
	private boolean isSelected;
	public String getNumber() {
		return number;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public void setNumber(String number) {

		this.number = number;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}
