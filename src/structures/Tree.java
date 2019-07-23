package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
			
		if (!sc.hasNext()){										// If the given HTML file is empty:			
			return;												// do nothing and return.
		}
		
		sc.useDelimiter("\n");									// Set Scanner "sc" to delimit by new lines.	
		root = build(new TagNode(sc.next(), null, null));		// Load the TagNode field "root" with the recursively built DOM Tree. 
		sc.close();												// Close the scanner.
	}
	
	/**
	 * Recursively builds the subtree (if there is one) that has the given TagNode "subRoot" as its root.
	 * Returns a link to the updated subRoot.
	 * 
	 * @param subRoot
	 * @param htmlTags
	 * @return
	 */
	private TagNode build(TagNode subRoot){ 
		
		if (!sc.hasNext()){														// If there's nothing left in the given HTML file to scan:			
			return subRoot;														// do nothing and return.
		}
		
		TagNode currNode = subRoot;												// Initialize a TagNode pointer "currNode" to 
																				// the given TagNode "subRoot".
		while(sc.hasNext()){													// Begin a loop to create the next TagNode; 
																				// handle siblings iteratively and children recursively.
			String currTag = currNode.tag;										// Initialize a string "currTag" to 
																				// the tag of "currNode" (including arrow brackets).
			String nextTag = sc.next();											// Initialize a string "nextTag" to 
																				// the tag of the next TagNode (including arrow brackets).
			if (nextTag.charAt(0) == '<' &&										// If "nextTag" is an HTML closing tag:
				nextTag.charAt(1) == '/' &&
				nextTag.charAt(nextTag.length()-1) == '>'){		
				
				return subRoot;													// return "subRoot".
			}
			
			if (currTag.charAt(0) == '<' &&										// If "currTag" is an HTML opening tag:
				currTag.charAt(1) != '/' &&
				currTag.charAt(currTag.length()-1) == '>'){				
				
				currNode.firstChild = 											// load "currNode" with the recursively built subtree
						build(new TagNode(nextTag, null, null));				// that has itself as its root.
				
				currNode.tag = 
						currNode.tag.substring(1, currNode.tag.length()-1);		// update "currNode.tag" by removing outer arrow brackets.
			}
			
			else if (currTag.charAt(0) != '<' &&								// Else if "currTag" is plain text or the tag of 
					currTag.charAt(currTag.length()-1) != '>'){					// a root of a completed subtree:
							
				currNode.sibling = new TagNode(nextTag, null, null);			// Create a new sibling TagNode and			
				currNode = currNode.sibling;									// update the "currNode" pointer.
			}	
		}
		
		return subRoot;															// Lastly, return subRoot (if not done already).
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		
		root = replaceTag(oldTag, newTag, root);		// Call the recursive "replaceTag" on the root and update it.
	}
	
	/**
	 * Recursively replaces all occurrences of an old tag with a new tag in a subtree with the root TagNode "subRoot".
	 * Returns a link to the updated subRoot.
	 * 
	 * @param oldTag
	 * @param newTag
	 * @param subRoot
	 * @return
	 */
	private TagNode replaceTag(String oldTag, String newTag, TagNode subRoot){		
		
		if (subRoot.tag.equals(oldTag)){								// If subRoot.tag is the oldTag:
			
			subRoot.tag = newTag;										// load it with the newTag.
		}
		
		if (subRoot.firstChild == null && 								// Then, if subRoot is a leaf:
			subRoot.sibling == null){									
			
			return subRoot;												// return subRoot.
		}
				
		if (subRoot.firstChild != null){								// Then, if subRoot has a first child:
					
			subRoot.firstChild = 										// call the recursive "replaceTag" on subRoot.firstChild and
					replaceTag(oldTag, newTag, subRoot.firstChild);		// update subRoot.firstChild.
		}																
				
		if (subRoot.sibling != null){									// Then, if subRoot has a first sibling:
					
			subRoot.sibling = 											// call the recursive "replaceTag" on subRoot.sibling and
					replaceTag(oldTag, newTag, subRoot.sibling);		// update subRoot.sibling.						
		}
		
		return subRoot;													// Lastly, return subRoot (if not done already).
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		
		TagNode tableNode = findTable(root);			// Call the recursive "findTable" on the root and initialize a 
														// new TagNode "tableNode" to the location of the TagNode with the tag "table".
		if (tableNode == null){							// If tableNode is null:
			
			return;										// do nothing and return.
		}
		
		TagNode rowNode = 								// Call the iterative "findRow" on tableNode.firstChild and 
				findRow(tableNode.firstChild, row);		// initialize a TagNode "rowNode" to the location of the specified row.
		
		if (rowNode == null){							// If rowNode is null:
			
			return;										// do nothing and return.
		}
		
		rowNode = boldRow(rowNode);						// Call the iterative "boldRow" on rowNode and update rowNode with the
														// location of the newly bolded row.
		return;											// Return.
	}
	
	/**
	 * Recursively searches for the TagNode with the tag "table" in a given subtree with root "subRoot".
	 * Returns a link to the TagNode with the tag "table" if there is one, otherwise returns null.
	 * 
	 * @param subRoot
	 * @return
	 */
	private TagNode findTable(TagNode subRoot){
		
		if (subRoot == null){						// If subRoot is null:
			
			return subRoot;							// return subRoot.
		}
		
		if (subRoot.tag.equals("table")){			// If subRoot.tag is "table":
			
			return subRoot;							// return subRoot.
		}
		
		TagNode sibSearch = 						// Call the recursive "findTable" on subRoot.sibling and initialize 
				findTable(subRoot.sibling);			// a new TagNode "sibSearch" to the location of the table (if there is one) within
													// the subtree that has subRoot.sibling as its root.
		TagNode chiSearch = 						// Call the recursive "findTable" on subRoot.firstChild and initialize 
				findTable(subRoot.firstChild);		// a new TagNode "chiSearch" to the location of the table (if there is one) within
													// the subtree that has subRoot.firstChild as its root.
		if (sibSearch != null){						// If sibSearch isn't null:
				
			return sibSearch;						// return sibSearch.
		}
			
		else if (chiSearch != null){				// Else if chiSearch isn't null:
				
			return chiSearch;						// return chiSearch.
		}
			
		else{										// Otherwise:
				
			return null;							// return null.
		}
	}
	
	/**
	 * Iteratively finds the row specified by the integer "row", searching through TagNode "firstRow" and its siblings.
	 * Returns a link to the TagNode "targetNode" that corresponds to the given row number, otherwise returns null.
	 * 
	 * @param firstRow
	 * @param row
	 * @return
	 */
	private TagNode findRow(TagNode firstRow, int row){
		
		if (row < 1){						// If the specified row number is less than 1:
			
			return null;					// return null.
		}
		
		TagNode targetNode = firstRow;		// Initialize a TagNode "targetNode" to firstRow.
		int rowCounter = 1;					// Initialize an integer "rowCounter" at 1.
		
		while (rowCounter != row){			// While the rowCounter doesn't equal the specified row number:
			
			targetNode = 					// load targetNode with the location of its sibling and
					targetNode.sibling;
			
			rowCounter++;					// increment rowCounter.
		}
		
		return targetNode;					// Lastly, return targetNode.
	}
	
	/**
	 * Iteratively boldfaces every column of the given row "rowNode".
	 * Returns a link to the updated rowNode.
	 * 
	 * @param rowNode
	 * @return
	 */
	private TagNode boldRow(TagNode rowNode){
		
		TagNode colNode = rowNode.firstChild;						// Initialize a new TagNode "colNode" to the location of
																	// rowNode's first child.
		if (colNode == null){										// If colNode is null:
			
			return rowNode;											// return rowNode.
		}
		
		while (colNode != null){									// While colNode isn't null:
			
			TagNode newNode = 										// initialize a new TagNode "newNode" with a "b" tag and
					new TagNode("b", colNode.firstChild, null);		// the same first child as colNode,
			
			colNode.firstChild = newNode;							// update colNode.firstChild to newNode, and
			colNode = colNode.sibling;								// update colNode to colNode.sibling.
		}
		
		return rowNode;												// Lastly, return rowNode.
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		
		root = removeTag(tag, root);		// Call the recursive "removeTag" on the root and update it.
	}
	
	/**
	 * Recursively removes all occurrences of a tag in a subtree with the root TagNode "subRoot".
	 * 
	 * @param tag
	 * @param subRoot
	 * @return
	 */
	private TagNode removeTag(String tag, TagNode subRoot){
			
		if (subRoot.firstChild == null && 
			subRoot.sibling == null){							// If subRoot is a leaf:
				
			return subRoot;										// return subRoot.
		}
			
		if (subRoot.firstChild != null){						// If subRoot has a first child:
				
			if (subRoot.tag.equals(tag)){						// if the subRoot's tag is the target tag:
					
				subRoot.tag = subRoot.firstChild.tag;			// change the subRoot's tag to its first child's tag,
				
				TagNode temp1 = subRoot.firstChild.sibling;		// initialize a temporary pointer with the location of 
																// the subRoot's first child's first sibling, and
				TagNode temp2 = subRoot.sibling;				// initialize a second temporary pointer with the location of 
																// the subRoot's first sibling.
					
				if (temp1 != null){								// Then, if the subRoot's first child has a sibling:
						
					while (temp1.sibling != null){				// while the subRoot's first child has another sibling:
							
						temp1 = temp1.sibling;					// load temp1 with the next sibling's location.
					}
						
					subRoot.sibling = 
							subRoot.firstChild.sibling;			// Then, load subRoot.sibling with the location of 
																// subRoot.firstChild.sibling and
					temp1.sibling = temp2;						// load subRoot.firstChild's last sibling's next sibling with
																// the location of subRoot's old first sibling.
				}	
										
				subRoot.firstChild = 
						subRoot.firstChild.firstChild;			// Then, load subRoot.firstChild with the location of
																// subRoot.firstChild.firstChild.
				
				if (tag.equals("ol") || tag.equals("ul")){		// If tag is "ol" or "ul":
					
					subRoot = replaceLITag(subRoot);			// call the recursive "removeLITag" on subRoot, update subRoot,
					subRoot = removeTag(tag, subRoot);			// call the recursive "removeTag" on subRoot, and update subRoot again.
				}
			}
				
			else{												// Otherwise subRoot isn't a leaf:
					
				subRoot.firstChild = 
						removeTag(tag, subRoot.firstChild);		// call the recursive "removeTag" on subRoot.firstChild and update
																// subRoot.firstChild.
			}
		}
			
		if (subRoot.sibling != null){							// Then, if subRoot has a first sibling:
				
			subRoot.sibling = 
					removeTag(tag, subRoot.sibling);			// call the recursive "removeTag" on it and update subRoot.sibling.
		}
		
		return subRoot;											// Lastly, return subRoot.
	}
	
	/**
	 * Recursively replaces all sibling occurrences of an "li" tag with a "p" tag, starting with and including the root TagNode "subRoot".
	 * 
	 * @param subRoot
	 * @return
	 */
	private TagNode replaceLITag(TagNode subRoot){		
		
		if (subRoot.tag.equals("li")){					// If subRoot.tag is "li":
			
			subRoot.tag = "p";							// load it with "p".
		}
		
		if (subRoot.sibling == null){					// Then, if subRoot has no first sibling:
			
			return subRoot;								// return subRoot.
		}
				
		else{											// Otherwise, subRoot has a first sibling:
					
			subRoot.sibling = 
					replaceLITag(subRoot.sibling);		// call the recursive "replaceTag" on subRoot.sibling and
														// update subRoot.sibling.
		}
		
		return subRoot;									// Lastly, return subRoot.
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		
		root = addTag(word, tag, root);
		return;
	}
	
	private TagNode addTag(String word, String tag, TagNode subRoot){
		
		if (subRoot == null){
			
			return subRoot;
		}
		
		if (subRoot.firstChild != null){
			
			if (!subRoot.tag.equals(tag)){
				
				subRoot.firstChild = addTag(word, tag, subRoot.firstChild);
			}
				
			subRoot.sibling = addTag(word, tag, subRoot.sibling);
			return subRoot;
		}
		
		String lowWord = word.toLowerCase();
		String lowUncheckedTag = subRoot.tag.toLowerCase();
			
		if (!lowUncheckedTag.contains(lowWord)){
				
			subRoot.sibling = addTag(word, tag, subRoot.sibling);
			return subRoot;
		}
			
		int wordLength = word.length();
		int wordBegex = lowUncheckedTag.indexOf(lowWord);
		int wordEndex = wordBegex + wordLength - 1;		
		String lSide = subRoot.tag.substring(0, wordBegex);
		String rSide = null;
		boolean isTaggable = true;
		
		while (wordBegex != -1){ 
		
			if (wordBegex != 0){									
						
				if (subRoot.tag.charAt(wordBegex - 1) != ' '){		
							
					isTaggable = false;								
				}
			}
		
			if (subRoot.tag.length() - 1 > wordEndex){				
					
				if (subRoot.tag.charAt(wordEndex + 1) == '.' ||		
					subRoot.tag.charAt(wordEndex + 1) == ',' ||
					subRoot.tag.charAt(wordEndex + 1) == '?' ||
					subRoot.tag.charAt(wordEndex + 1) == '!' ||
					subRoot.tag.charAt(wordEndex + 1) == ':' ||
					subRoot.tag.charAt(wordEndex + 1) == ';'){
						
					wordEndex++;									
				}
			}

			if (subRoot.tag.length() - 1 > wordEndex){				
						
				if (subRoot.tag.charAt(wordEndex + 1) != ' '){		
							
					isTaggable = false;								
				}
			}
					
			if (!isTaggable){																
						
				lSide += subRoot.tag.substring(wordBegex, wordEndex + 1);					
				lowUncheckedTag = subRoot.tag.substring(wordEndex + 1).toLowerCase();		
				
				if (!lowUncheckedTag.contains(lowWord)){
					
					subRoot.sibling = addTag(word, tag, subRoot.sibling);
					return subRoot;
				}
				
				else{
					
					wordBegex = lowUncheckedTag.indexOf(lowWord) + lSide.length();
					wordEndex = wordBegex + wordLength - 1;	
					isTaggable = true;
				}
			}
			
			else{
				
				break;
			}
		}
		
		rSide = subRoot.tag.substring(wordEndex + 1);
		
		if (wordBegex == 0){
			
			if (wordEndex == subRoot.tag.length()-1){
				
				subRoot.firstChild = new TagNode(subRoot.tag, null, null);
				subRoot.tag = tag;
				subRoot.sibling = addTag(word, tag, subRoot.sibling);
				return subRoot;
			}
			
			else{
				
				subRoot.firstChild = new TagNode(subRoot.tag.substring(0, wordEndex + 1), null, null);
				TagNode rSideNode = new TagNode(rSide, null, subRoot.sibling);
				subRoot.tag = tag;
				subRoot.sibling = rSideNode;
				subRoot.sibling = addTag(word, tag, subRoot.sibling);
				return subRoot;
			}
		}
		
		else{
		
			TagNode taggedTextNode = new TagNode(subRoot.tag.substring(wordBegex, wordEndex + 1), null, null);
			TagNode rSideNode = new TagNode(rSide, null, subRoot.sibling);
			TagNode htmlTagNode = new TagNode(tag, taggedTextNode, rSideNode);
			subRoot.tag = lSide;
			subRoot.sibling = htmlTagNode;
			subRoot.sibling = addTag(word, tag, subRoot.sibling);
			return subRoot;
		}
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|---- ");
			} else {
				System.out.print("      ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
