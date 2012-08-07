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
package com.atlascopco.literumilo.ui.painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LayeredHighlighter.LayerPainter;
import javax.swing.text.Position;
import javax.swing.text.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link HighlightPainter} paints a squiggly line to show the words that
 * are misspelled.
 *
 * @author Thomas Joiner
 */
public class SquigglyUnderlineHighlightPainter extends LayerPainter {

	private static final BasicStroke STROKE_1_2 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 2.0f, new float[] { 1.0f, 2.0f }, 0.0f);
	private static final BasicStroke STROKE_2_4_0 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 2.0f, new float[] { 2.0f, 4.0f }, 0.0f);
	private static final BasicStroke STROKE_2_4_1 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 2.0f, new float[] { 2.0f, 4.0f }, 1.0f);

	private static final Logger log = LoggerFactory.getLogger(SquigglyUnderlineHighlightPainter.class);

	private final Color underlineColor;

	public SquigglyUnderlineHighlightPainter(Color underlineColor) {
		if ( underlineColor == null ) {
			throw new NullPointerException("underlineColor must be non-null.");
		}

		this.underlineColor = underlineColor;
	}

	@Override
	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			TextUI textUI = c.getUI();
			Rectangle rectangle0 = textUI.modelToView(c, p0);
			Rectangle rectangle1 = textUI.modelToView(c, p1);
			Rectangle alloc = bounds.getBounds();

			g.setColor(this.getUnderlineColor());

			if (rectangle0.y == rectangle1.y) {
				// same line, render a rectangle
				Rectangle r = rectangle0.union(rectangle1);

				this.renderLine(g, r.x, r.y + r.height - 3, r.width);
			} else {
				// different lines
				int p0ToMarginWidth = alloc.x + alloc.width - rectangle0.x;
				this.renderLine(g, rectangle0.x, rectangle0.y, p0ToMarginWidth);

				for ( int y = rectangle0.y + rectangle0.height; y < rectangle1.y; y+=rectangle0.height) {
					this.renderLine(g, alloc.x, y, alloc.width);
				}

				this.renderLine(g, alloc.x, rectangle1.y, (rectangle1.x - alloc.x));
			}
		} catch (BadLocationException e) {
			log.warn("Error occurred when rendering line.", e);
		}
	}

	@Override
	public Shape paintLayer(Graphics g, int p0, int p1, Shape viewBounds,
			JTextComponent editor, View view) {
		Color color = this.getUnderlineColor();

		if (color == null) {
			g.setColor(editor.getSelectionColor());
		} else {
			g.setColor(color);
		}

		if (p0 == view.getStartOffset() && p1 == view.getEndOffset()) {
			// Contained in view, can just use bounds.
			Rectangle bounds;

			if (viewBounds instanceof Rectangle) {
				bounds = (Rectangle) viewBounds;
			} else {
				bounds = viewBounds.getBounds();
			}

			this.renderLine(g, bounds.x, bounds.y + bounds.height - 3,
					bounds.width);

			return bounds;
		} else {
			// Should only render part of View.
			try {
				// Determine the area that we need to render based on the
				// viewBounds
				// that we received.
				Shape shape = view.modelToView(p0, Position.Bias.Forward, p1,
						Position.Bias.Backward, viewBounds);

				Rectangle bounds = (shape instanceof Rectangle) ? (Rectangle) shape
						: shape.getBounds();

				this.renderLine(g, bounds.x, bounds.y + bounds.height - 3,
						bounds.width);

				return bounds;
			} catch (BadLocationException e) {
				// can't render
				log.warn("Error occurred when rendering line.", e);
			}
		}
		// Only if exception
		return null;
	}

	/**
	 * Draw a squiggly line of the given width at the given (x,y) coordinates.
	 * The line will be 3 pixels tall.
	 *
	 * @param g
	 *            the canvas to draw it on
	 * @param x
	 *            the x coordinate to start at
	 * @param y
	 *            the y coordinate to start at
	 * @param width
	 *            the width that the line should extend.
	 */
	private void renderLine(Graphics g, int x, int y, int width) {
		if (g instanceof Graphics2D) {
			// Create a copy of the graphics context so we don't
			// mess with the original settings.
			Graphics2D g2d = (Graphics2D) g.create();

			try {
				BasicStroke stroke2_4_1 = STROKE_2_4_1;
				BasicStroke stroke2_4_0 = STROKE_2_4_0;
				BasicStroke stroke1_2 = STROKE_1_2;

				g2d.setColor(this.getUnderlineColor());
				g2d.setStroke(stroke2_4_1);
				g2d.drawLine(x, y, x + width, y);
				g2d.setStroke(stroke1_2);
				g2d.drawLine(x + 1, y + 1, x + width - 1, y + 1);
				g2d.setStroke(stroke2_4_0);
				g2d.drawLine(x + 2, y + 2, x + width - 2, y + 2);
			} finally {
				g2d.dispose();
			}
		} else {
			g.setColor(this.getUnderlineColor());
			int xMax = x + width;
			for (int i = 0; i < width; i += 4) {
				g.drawLine(x + i, y, Math.min(xMax, x + i + 2), y);
				if (x + i + 2 < xMax) {
					g.drawLine(x + i + 2, y + 1, Math.min(xMax, x + i + 4), y + 1);
				}
			}
		}
	}

	public Color getUnderlineColor() {
		return this.underlineColor;
	}

}
