package fr.insa.ocm.model.utils;

import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.ArrayList;
import java.util.List;


public class Rank<X extends Pattern> extends ArrayList<X> {

	public Rank(){
		super();
	}

	public Rank(int size){
		super(size);
	}

	public Rank(Rank<X> rank){ super(rank); }

	public Rank(List<X> list){ super(list); }
}