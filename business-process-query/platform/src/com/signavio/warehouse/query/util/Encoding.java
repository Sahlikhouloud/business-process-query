package com.signavio.warehouse.query.util;

public class Encoding {
	private static final String S_SEQ = "a";
	private static final String S_AND_SPLIT = "b";
	private static final String S_AND_JOIN = "c";
	private static final String S_XOR_SPLIT = "d";
	private static final String S_XOR_JOIN = "e";
	private static final String S_OR_SPLIT = "f";
	private static final String S_OR_JOIN = "g";

	private static final String SEQ_S = "h";
	private static final String AND_SPLIT_S = "i";
	private static final String AND_JOIN_S = "j";
	private static final String XOR_SPLIT_S = "k";
	private static final String XOR_JOIN_S = "l";
	private static final String OR_SPLIT_S = "m";
	private static final String OR_JOIN_S = "n";

	private static final String AND_SPLIT_AND_SPLIT = "o";
	private static final String AND_SPLIT_AND_JOIN = "p";
	private static final String AND_SPLIT_XOR_SPLIT = "q";
	private static final String AND_SPLIT_XOR_JOIN = "r";
	private static final String AND_SPLIT_OR_SPLIT = "s";
	private static final String AND_SPLIT_OR_JOIN = "t";

	private static final String AND_JOIN_AND_SPLIT = "u";
	private static final String AND_JOIN_AND_JOIN = "v";
	private static final String AND_JOIN_XOR_SPLIT = "w";
	private static final String AND_JOIN_XOR_JOIN = "x";
	private static final String AND_JOIN_OR_SPLIT = "y";
	private static final String AND_JOIN_OR_JOIN = "z";

	private static final String XOR_SPLIT_AND_SPLIT = "1";
	private static final String XOR_SPLIT_AND_JOIN = "2";
	private static final String XOR_SPLIT_XOR_SPLIT = "3";
	private static final String XOR_SPLIT_XOR_JOIN = "4";
	private static final String XOR_SPLIT_OR_SPLIT = "5";
	private static final String XOR_SPLIT_OR_JOIN = "6";

	private static final String XOR_JOIN_AND_SPLIT = "7";
	private static final String XOR_JOIN_AND_JOIN = "8";
	private static final String XOR_JOIN_XOR_SPLIT = "9";
	private static final String XOR_JOIN_XOR_JOIN = "!";
	private static final String XOR_JOIN_OR_SPLIT = "@";
	private static final String XOR_JOIN_OR_JOIN = "#";

	private static final String OR_SPLIT_AND_SPLIT = "$";
	private static final String OR_SPLIT_AND_JOIN = "%";
	private static final String OR_SPLIT_XOR_SPLIT = "^";
	private static final String OR_SPLIT_XOR_JOIN = "&";
	private static final String OR_SPLIT_OR_SPLIT = "*";
	private static final String OR_SPLIT_OR_JOIN = "(";

	private static final String OR_JOIN_AND_SPLIT = ")";
	private static final String OR_JOIN_AND_JOIN = "_";
	private static final String OR_JOIN_XOR_SPLIT = "+";
	private static final String OR_JOIN_XOR_JOIN = "-";
	private static final String OR_JOIN_OR_SPLIT = "[";
	private static final String OR_JOIN_OR_JOIN = "]";

	private static String encondingPairToBeSingleCharecter(String atomicPattern) {
		String encode = null;

		if (atomicPattern.equals("s,seq")) {
			encode = S_SEQ;
		} else if (atomicPattern.equals("s,AND-split")) {
			encode = S_AND_SPLIT;
		} else if (atomicPattern.equals("s,AND-join")) {
			encode = S_AND_JOIN;
		} else if (atomicPattern.equals("s,XOR-split")) {
			encode = S_XOR_SPLIT;
		} else if (atomicPattern.equals("s,XOR-join")) {
			encode = S_XOR_JOIN;
		} else if (atomicPattern.equals("s,OR-split")) {
			encode = S_OR_SPLIT;
		} else if (atomicPattern.equals("s,OR-join")) {
			encode = S_OR_JOIN;
		} else if (atomicPattern.equals("seq,s")) {
			encode = SEQ_S;
		} else if (atomicPattern.equals("AND-split,s")) {
			encode = AND_SPLIT_S;
		} else if (atomicPattern.equals("AND-join,s")) {
			encode = AND_JOIN_S;
		} else if (atomicPattern.equals("XOR-split,s")) {
			encode = XOR_SPLIT_S;
		} else if (atomicPattern.equals("XOR-join,s")) {
			encode = XOR_JOIN_S;
		} else if (atomicPattern.equals("OR-split,s")) {
			encode = OR_SPLIT_S;
		} else if (atomicPattern.equals("OR-join,s")) {
			encode = OR_JOIN_S;
		} else if (atomicPattern.equals("AND-split,AND-split")) {
			encode = AND_SPLIT_AND_SPLIT;
		} else if (atomicPattern.equals("AND-split,AND-join")) {
			encode = AND_SPLIT_AND_JOIN;
		} else if (atomicPattern.equals("AND-split,XOR-split")) {
			encode = AND_SPLIT_XOR_SPLIT;
		} else if (atomicPattern.equals("AND-split,XOR-join")) {
			encode = AND_SPLIT_XOR_JOIN;
		} else if (atomicPattern.equals("AND-split,OR-split")) {
			encode = AND_SPLIT_OR_SPLIT;
		} else if (atomicPattern.equals("AND-split,OR-join")) {
			encode = AND_SPLIT_OR_JOIN;
		} else if (atomicPattern.equals("AND-join,AND-split")) {
			encode = AND_JOIN_AND_SPLIT;
		} else if (atomicPattern.equals("AND-join,AND-join")) {
			encode = AND_JOIN_AND_JOIN;
		} else if (atomicPattern.equals("AND-join,XOR-split")) {
			encode = AND_JOIN_XOR_SPLIT;
		} else if (atomicPattern.equals("AND-join,XOR-join")) {
			encode = AND_JOIN_XOR_JOIN;
		} else if (atomicPattern.equals("AND-join,OR-split")) {
			encode = AND_JOIN_OR_SPLIT;
		} else if (atomicPattern.equals("AND-join,OR-join")) {
			encode = AND_JOIN_OR_JOIN;
		} else if (atomicPattern.equals("XOR-split,AND-split")) {
			encode = XOR_SPLIT_AND_SPLIT;
		} else if (atomicPattern.equals("XOR-split,AND-join")) {
			encode = XOR_SPLIT_AND_JOIN;
		} else if (atomicPattern.equals("XOR-split,XOR-split")) {
			encode = XOR_SPLIT_XOR_SPLIT;
		} else if (atomicPattern.equals("XOR-split,XOR-join")) {
			encode = XOR_SPLIT_XOR_JOIN;
		} else if (atomicPattern.equals("XOR-split,OR-split")) {
			encode = XOR_SPLIT_OR_SPLIT;
		} else if (atomicPattern.equals("XOR-split,OR-join")) {
			encode = XOR_SPLIT_OR_JOIN;
		} else if (atomicPattern.equals("XOR-join,AND-split")) {
			encode = XOR_JOIN_AND_SPLIT;
		} else if (atomicPattern.equals("XOR-join,AND-join")) {
			encode = XOR_JOIN_AND_JOIN;
		} else if (atomicPattern.equals("XOR-join,XOR-split")) {
			encode = XOR_JOIN_XOR_SPLIT;
		} else if (atomicPattern.equals("XOR-join,XOR-join")) {
			encode = XOR_JOIN_XOR_JOIN;
		} else if (atomicPattern.equals("XOR-join,OR-split")) {
			encode = XOR_JOIN_OR_SPLIT;
		} else if (atomicPattern.equals("XOR-join,OR-join")) {
			encode = XOR_JOIN_OR_JOIN;
		} else if (atomicPattern.equals("OR-split,AND-split")) {
			encode = OR_SPLIT_AND_SPLIT;
		} else if (atomicPattern.equals("OR-split,AND-join")) {
			encode = OR_SPLIT_AND_JOIN;
		} else if (atomicPattern.equals("OR-split,XOR-split")) {
			encode = OR_SPLIT_XOR_SPLIT;
		} else if (atomicPattern.equals("OR-split,XOR-join")) {
			encode = OR_SPLIT_XOR_JOIN;
		} else if (atomicPattern.equals("OR-split,OR-split")) {
			encode = OR_SPLIT_OR_SPLIT;
		} else if (atomicPattern.equals("OR-split,OR-join")) {
			encode = OR_SPLIT_OR_JOIN;
		} else if (atomicPattern.equals("OR-join,AND-split")) {
			encode = OR_JOIN_AND_SPLIT;
		} else if (atomicPattern.equals("OR-join,AND-join")) {
			encode = OR_JOIN_AND_JOIN;
		} else if (atomicPattern.equals("OR-join,XOR-split")) {
			encode = OR_JOIN_XOR_SPLIT;
		} else if (atomicPattern.equals("OR-join,XOR-join")) {
			encode = OR_JOIN_XOR_JOIN;
		} else if (atomicPattern.equals("OR-join,OR-split")) {
			encode = OR_JOIN_OR_SPLIT;
		} else if (atomicPattern.equals("OR-join,OR-join")) {
			encode = OR_JOIN_OR_JOIN;
		} else{
			System.out.println("Cannot encode pattern : "+atomicPattern);
		}

		return encode;
	}
	
	//format: AND-join,s||AND-split,AND-join||XOR-split,AND-split||XOR-split,AND-split||AND-split,AND-join||AND-join,s
	public static String encode(String pattern){
		String encodedString = "";
		String [] atomicPatterns = pattern.split("\\|\\|");
		for(String atomicPattern : atomicPatterns){
			encodedString += encondingPairToBeSingleCharecter(atomicPattern);
		}
		return encodedString;
	}

}
