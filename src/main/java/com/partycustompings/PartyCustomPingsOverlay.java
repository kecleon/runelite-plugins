/*
 * Copyright (c) 2024, kecleon, jackvolonte
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.partycustompings;

import lombok.extern.slf4j.Slf4j;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

@Slf4j
public class PartyCustomPingsOverlay extends Overlay
{
	private final Client client;
	private final PartyCustomPingsPlugin plugin;

	private final Color TEMP_TILE_COLOR = new Color(2, 217, 134);
	private final Color OVAL_INNER_LINE_COLOR = new Color(205, 178, 128);
	private final Color OVAL_OUTER_LINE_COLOR = new Color(37, 56, 33);
	private final Color OVAL_FILL_COLOR = new Color(19, 45, 46, 20);

	@Inject
	public PartyCustomPingsOverlay(final Client client, final PartyCustomPingsPlugin plugin)
	{
		this.client = client;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(final Graphics2D graphics)
	{
		if (plugin.wheelOpened == -1)
		{
			return null;
		}

		log.debug("Rendering wheel at point {}", plugin.screenPoint.toString());

		renderTile(graphics, plugin.tilePoint);
		renderWheel(graphics, plugin.screenPoint);

		return null;
	}

	private void renderTile(final Graphics2D graphics, final Tile tile)
	{
		final LocalPoint localPoint = tile.getLocalLocation();

		if (localPoint == null)
		{
			log.debug("localpoint null");
			return;
		}

		final Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);

		if (poly == null)
		{
			log.debug("poly null");
			return;
		}

		log.debug("poly: {}", poly);
		OverlayUtil.renderPolygon(graphics, poly, TEMP_TILE_COLOR);
	}

	private void renderWheel(Graphics2D graphics, Point screenPoint)
	{
		graphics.setColor(Color.WHITE);
		graphics.drawString("PING", screenPoint.getX() - 10, screenPoint.getY() - 2);

		graphics.setColor(OVAL_FILL_COLOR);
		graphics.fillOval(screenPoint.getX() - 84, screenPoint.getY() - 84, 168, 168);

		graphics.setColor(OVAL_INNER_LINE_COLOR);
		graphics.drawOval(screenPoint.getX() - 42, screenPoint.getY() - 42, 84, 84);

		graphics.setColor(OVAL_OUTER_LINE_COLOR);
		graphics.drawOval(screenPoint.getX() - 84, screenPoint.getY() - 84, 168, 168);
	}
}
