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
package com.atlascopco.literumilo;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.junit.Test;

import com.atlascopco.literumilo.tokenizer.DefaultTokenizer;
import com.atlascopco.literumilo.tokenizer.WordToken;

public class DefaultTokenizerTest {

	private static final String TEST_STRING = "Mein Hut, der hat drei Ecken.";

	@Test
	public void testGetTokensWithFullString() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 0, mockument.getLength()));

		assertThat(tokens.size(), is(6));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("Mein"));
		assertThat(firstToken.getStartOffset().getOffset(), is(0));
		assertThat(firstToken.getLength(), is(4));

		WordToken fifthToken = tokens.get(4);

		assertThat(fifthToken.getToken(), is("drei"));
		assertThat(fifthToken.getStartOffset().getOffset(), is(18));
		assertThat(fifthToken.getLength(), is(4));
	}

	/**
	 * "er hat "
	 * @throws BadLocationException
	 */
	@Test
	public void testGetTokensPartialStringMultipleWordsBeginningInMiddle() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 11, 7));

		assertThat(tokens.size(), is(2));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("der"));
		assertThat(firstToken.getStartOffset().getOffset(), is(10));
		assertThat(firstToken.getLength(), is(3));

		WordToken fifthToken = tokens.get(1);

		assertThat(fifthToken.getToken(), is("hat"));
		assertThat(fifthToken.getStartOffset().getOffset(), is(14));
		assertThat(fifthToken.getLength(), is(3));
	}
	/**
	 * "er ha"
	 * @throws BadLocationException
	 */
	@Test
	public void testGetTokensPartialStringMultipleWordsBeginningEndInMiddle() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 11, 5));

		assertThat(tokens.size(), is(2));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("der"));
		assertThat(firstToken.getStartOffset().getOffset(), is(10));
		assertThat(firstToken.getLength(), is(3));

		WordToken fifthToken = tokens.get(1);

		assertThat(fifthToken.getToken(), is("hat"));
		assertThat(fifthToken.getStartOffset().getOffset(), is(14));
		assertThat(fifthToken.getLength(), is(3));
	}
	/**
	 * " der ha"
	 * @throws BadLocationException
	 */
	@Test
	public void testGetTokensPartialStringMultipleWordsEndInMiddle() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 9, 7));

		assertThat(tokens.size(), is(2));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("der"));
		assertThat(firstToken.getStartOffset().getOffset(), is(10));
		assertThat(firstToken.getLength(), is(3));

		WordToken fifthToken = tokens.get(1);

		assertThat(fifthToken.getToken(), is("hat"));
		assertThat(fifthToken.getStartOffset().getOffset(), is(14));
		assertThat(fifthToken.getLength(), is(3));
	}
	/**
	 * " der hat "
	 * @throws BadLocationException
	 */
	@Test
	public void testGetTokensPartialStringMultipleWordsNeitherInMiddle() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 9, 9));

		assertThat(tokens.size(), is(2));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("der"));
		assertThat(firstToken.getStartOffset().getOffset(), is(10));
		assertThat(firstToken.getLength(), is(3));

		WordToken fifthToken = tokens.get(1);

		assertThat(fifthToken.getToken(), is("hat"));
		assertThat(fifthToken.getStartOffset().getOffset(), is(14));
		assertThat(fifthToken.getLength(), is(3));
	}
	/**
	 * "der "
	 * @throws BadLocationException
	 */
	@Test
	public void testGetTokensPartialStringSingleWordBeginningInMiddle() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 10, 4));

		assertThat(tokens.size(), is(1));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("der"));
		assertThat(firstToken.getStartOffset().getOffset(), is(10));
		assertThat(firstToken.getLength(), is(3));
	}
	/**
	 * "der"
	 * @throws BadLocationException
	 */
	@Test
	public void testGetTokensPartialStringSingleWordBeginningEndInMiddle() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 10, 3));

		assertThat(tokens.size(), is(1));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("der"));
		assertThat(firstToken.getStartOffset().getOffset(), is(10));
		assertThat(firstToken.getLength(), is(3));
	}
	/**
	 * " der"
	 * @throws BadLocationException
	 */
	@Test
	public void testGetTokensPartialStringSingleWordEndInMiddle() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 9, 4));

		assertThat(tokens.size(), is(1));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("der"));
		assertThat(firstToken.getStartOffset().getOffset(), is(10));
		assertThat(firstToken.getLength(), is(3));
	}
	/**
	 * " der "
	 * @throws BadLocationException
	 */
	@Test
	public void testGetTokensPartialStringSingleWordNeitherInMiddle() throws BadLocationException {
		Mockument mockument = new Mockument(TEST_STRING);

		List<WordToken> tokens = this.toList(new DefaultTokenizer().tokenize(mockument, 9, 5));

		assertThat(tokens.size(), is(1));

		WordToken firstToken = tokens.get(0);

		assertThat(firstToken.getToken(), is("der"));
		assertThat(firstToken.getStartOffset().getOffset(), is(10));
		assertThat(firstToken.getLength(), is(3));
	}

	private <T> List<T> toList(Iterable<T> iterable) {
		List<T> list = new ArrayList<T>();

		for (T t : iterable) {
			list.add(t);
		}

		return list;
	}

}
