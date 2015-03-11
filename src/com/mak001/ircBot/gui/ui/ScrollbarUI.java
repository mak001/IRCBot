package com.mak001.ircbot.gui.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ScrollbarUI extends BasicScrollBarUI {

	public final Color thumbHighlight = new Color(255, 0, 0, 200),
			thumb = thumbHighlight.darker(), thumbLightShadow = thumb.darker(),
			thumbDarkShadow = thumbLightShadow.darker(), track = (new Color(0,
					0, 0, 100)).brighter(), trackHighlight = (new Color(
					Color.GRAY.getRed(), Color.GRAY.getGreen(),
					Color.GRAY.getBlue(), 100));
	private final Dimension dim15 = new Dimension(15, 15);

	@Override
	protected void configureScrollBarColors() {
		thumbHighlightColor = thumbHighlight;
		thumbLightShadowColor = thumbLightShadow;
		thumbDarkShadowColor = thumbDarkShadow;
		thumbColor = thumb;
		trackColor = track;
		trackHighlightColor = trackHighlight;
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();
		maximumThumbSize = minimumThumbSize = dim15;
		scrollbar.setOpaque(false);
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		final BasicArrowButton decrease = new BasicArrowButton(orientation,
				thumb, thumbLightShadow, thumbDarkShadow, thumbHighlight) {
			private static final long serialVersionUID = 1L;

			public Dimension getPreferredSize() {
				return dim15;
			}

			public Dimension getMinimumSize() {
				return dim15;
			}

			public Dimension getMaximumSize() {
				return dim15;
			}
		};
		decrease.setOpaque(false);
		return decrease;
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		final BasicArrowButton increase = new BasicArrowButton(orientation,
				thumb, thumbLightShadow, thumbDarkShadow, thumbHighlight) {
			private static final long serialVersionUID = 1L;

			public Dimension getPreferredSize() {
				return dim15;
			}

			public Dimension getMinimumSize() {
				return dim15;
			}

			public Dimension getMaximumSize() {
				return dim15;
			}
		};
		increase.setOpaque(false);
		return increase;
	}
}