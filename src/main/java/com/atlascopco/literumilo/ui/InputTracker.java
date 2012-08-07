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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.event.PopupMenuEvent;

/**
 * This class is a kludge to try to figure out what it was that initiated the popup, since
 * the {@link PopupMenuEvent} does not tell us what event caused it.
 *
 * This is necessary for figuring out what word to provide spelling suggestions for.
 *
 * @author Thomas Joiner
 */
final class InputTracker implements KeyListener, MouseListener, MouseMotionListener {

	private boolean keyboardPressedLast = false;

	@Override
	public void mouseDragged(MouseEvent e) {
		this.keyboardPressedLast = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.keyboardPressedLast = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.keyboardPressedLast = true;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.keyboardPressedLast = false;
	}

	public boolean isKeyboardTrigger() {
		return this.keyboardPressedLast;
	}

	@Override
	public void keyTyped(KeyEvent e) { /* nothing to do */ }
	@Override
	public void keyReleased(KeyEvent e) { /* nothing to do */ }
	@Override
	public void mouseClicked(MouseEvent e) { /* nothing to do */ }
	@Override
	public void mouseReleased(MouseEvent e) { /* nothing to do */ }
	@Override
	public void mouseEntered(MouseEvent e) { /* nothing to do */ }
	@Override
	public void mouseExited(MouseEvent e) { /* nothing to do */ }

}
