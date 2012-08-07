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
package com.atlascopco.literumilo.ui;

import java.awt.event.ActionEvent;
import java.util.Locale;

import javax.swing.AbstractAction;

import com.atlascopco.literumilo.spellchecker.Spellchecker;

/**
 * This action sets the current language of the spell-checker.
 *
 * @author Thomas Joiner
 */
class LanguageSelector extends AbstractAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 4801814280462112289L;

	/**
	 * The {@link Spellchecker} to change the selected language of.
	 */
	private final Spellchecker checker;
	/**
	 * The {@link Locale} to switch the {@link Spellchecker} to.
	 */
	private final Locale toSelect;

	/**
	 * This action will change the specified checker's language to the
	 * specified locale.
	 *
	 * @param checker the checker to change the language of
	 * @param toSelect the {@link Locale} to switch the spellchecker to
	 */
	public LanguageSelector(Spellchecker checker, Locale toSelect) {
		super(toSelect.getDisplayLanguage(toSelect));
		this.checker = checker;
		this.toSelect = toSelect;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.checker.setDictionary(this.toSelect);
	}
}
