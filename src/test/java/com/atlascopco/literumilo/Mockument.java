/**
 * Copyright [yyyy] Atlas Copco Drilling Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atlascopco.literumilo;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;

public class Mockument implements Document {

	private final String document;

	public Mockument(String document) {
		this.document = document;
	}

	@Override
	public int getLength() {
		return this.document.length();
	}

	@Override
	public void addDocumentListener(DocumentListener listener) {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public void removeDocumentListener(DocumentListener listener) {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public void addUndoableEditListener(UndoableEditListener listener) {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public void removeUndoableEditListener(UndoableEditListener listener) {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public Object getProperty(Object key) {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public void putProperty(Object key, Object value) {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public void remove(int offs, int len) throws BadLocationException {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public void insertString(int offset, String str, AttributeSet a)
			throws BadLocationException {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public String getText(int offset, int length) throws BadLocationException {
		return this.document.substring(offset, offset + length);
	}

	@Override
	public void getText(int offset, int length, Segment txt)
			throws BadLocationException {
		txt.array = this.document.toCharArray();
		txt.offset = offset;
		txt.count = length;
	}

	@Override
	public Position getStartPosition() {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public Position getEndPosition() {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public Position createPosition(final int offs) throws BadLocationException {
		return new Position() {
			@Override
			public int getOffset() {
				// TODO Auto-generated method stub
				return offs;
			}
		};
	}

	@Override
	public Element[] getRootElements() {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public Element getDefaultRootElement() {
		throw new IllegalStateException("Not implemented.");
	}

	@Override
	public void render(Runnable r) {
		throw new IllegalStateException("Not implemented.");
	}

}
