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

import javax.swing.text.JTextComponent;

import com.atlascopco.literumilo.spellchecker.Spellchecker;
import com.atlascopco.literumilo.tokenizer.DefaultTokenizer;
import com.atlascopco.literumilo.tokenizer.Tokenizer;
import com.atlascopco.literumilo.ui.SpellcheckedComponent;

/**
 * This class is the main interface to the spell-checking.
 *
 * @author Thomas Joiner
 */
public class Literumilo {

	/**
	 * The {@link Tokenizer} to use to tokenize text.
	 */
	private static Tokenizer tokenizer = new DefaultTokenizer();
	/**
	 * The {@link Spellchecker} to use to perform the spell-checking.
	 */
	private static Spellchecker spellchecker;

	/**
	 * This method will activate spell-checking underlining on a component and
	 * register the context menus.
	 *
	 * @param component the component to activate spell-checking on
	 * @return a {@link SpellcheckedComponent} instance which can be used to manipulate the component.
	 */
	public static SpellcheckedComponent register(JTextComponent component) {
		return register(component, true);
	}

	/**
	 * This method will activate spell-checking on a component and depending upon
	 * the {@code registerMenus} parameter,  will or will not register context menus.
	 *
	 * @param component the component to activate spell-checking on
	 * @param registerMenus true if you want context menus to be registered
	 * @return a {@link SpellcheckedComponent} instance which can be used to manipulate the component.
	 */
	public static SpellcheckedComponent register(final JTextComponent component, boolean registerMenus) {
		if ( spellchecker == null || tokenizer == null ) {
			throw new IllegalStateException("The spellchecker and the tokenizer must be initialized before registering components.");
		}

		SpellcheckedComponent spellcheckedComponent = new SpellcheckedComponent(component, spellchecker, tokenizer, registerMenus);

		// Initialize the component.
		spellcheckedComponent.initialize();

		return spellcheckedComponent;
	}

	/**
	 * Set the {@link Tokenizer} that should be used to tokenize text in the components.
	 *
	 * @param tokenizer the {@link Tokenizer} to use
	 */
	public static void setTokenizer(Tokenizer tokenizer) {
		Literumilo.tokenizer = tokenizer;
	}

	/**
	 * Set the {@link Spellchecker} that should be used to perform spell-checks on the component.
	 * @param spellchecker
	 */
	public static void setSpellchecker(Spellchecker spellchecker) {
		Literumilo.spellchecker = spellchecker;
	}

	/**
	 * Get the {@link Tokenizer} that is currently being used to tokenize text.
	 * @return the {@link Tokenizer} currently in use
	 */
	public static Tokenizer getTokenizer() {
		return tokenizer;
	}

	/**
	 * Get the {@link Spellchecker} that is currently being used to spell-check text.
	 * @return the {@link Spellchecker} currently in use.
	 */
	public static Spellchecker getSpellchecker() {
		return spellchecker;
	}
}
