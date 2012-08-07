/*******************************************************************************
 * Copyright 2012 Atlas Copco Drilling Solutions
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.atlascopco.literumilo.tokenizer;

import java.util.List;

import javax.swing.text.Document;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Position;

/**
 * This class represents a token in a {@link Document}. This class
 * provides the placement of the token as well as the token itself.
 *
 * @author Thomas Joiner
 *
 */
public class WordToken {
	private Position startOffset;
	private Position endOffset;
	private String token;
	private List<String> suggestions;
	private Highlight highlightTag;

	public Position getStartOffset() {
		return this.startOffset;
	}

	public void setStartOffset(Position startOffset) {
		this.startOffset = startOffset;
	}

	public Position getEndOffset() {
		return this.endOffset;
	}

	public void setEndOffset(Position endOffset) {
		this.endOffset = endOffset;
	}

	public int getLength() {
		return this.token.length();
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<String> getSuggestions() {
		return this.suggestions;
	}

	public void setSuggestions(List<String> suggestions) {
		this.suggestions = suggestions;
	}

	public Highlight getHighlightTag() {
		return this.highlightTag;
	}

	public boolean hasChanged() {
		return this.getEndOffset().getOffset() - this.getStartOffset().getOffset() + 1 != this.token.length();
	}

	public void setHighlightTag(Object highlightTag) {
		if ( highlightTag instanceof Highlight ) {
			this.highlightTag = (Highlight) highlightTag;
		} else {
			throw new IllegalArgumentException("Highlight tag must be of type Highlighter.Highlight (actually was "+highlightTag.getClass()+").");
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getLength();
		result = prime * result + ((this.startOffset == null) ? 0 : this.startOffset.getOffset());
		result = prime * result + ((this.token == null) ? 0 : this.token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		WordToken other = (WordToken) obj;
		if (this.getLength() != other.getLength()) {
			return false;
		}
		if (this.startOffset.getOffset() != other.startOffset.getOffset()) {
			return false;
		}
		if (this.token == null) {
			if (other.token != null) {
				return false;
			}
		} else if (!this.token.equals(other.token)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "WordToken [offset=" + this.startOffset + ", length=" + this.getLength()
				+ ", token=" + this.token + "]";
	}




}
