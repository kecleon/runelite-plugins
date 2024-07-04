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

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.events.FocusChanged;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.config.Keybind;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.HotkeyListener;

@Slf4j
@PluginDescriptor(
		name = "Party Custom Pings",
		description = "A plugin to share more types of pings in parties, including images.",
		tags = {"party", "ping", "bait"}
)
public class PartyCustomPingsPlugin extends Plugin
{
	protected static final String CONFIG_GROUP = "partycustompings";

	@Inject
	private Client client;

	@Inject
	private PartyCustomPingsConfig config;

	@Inject
	private PartyService party;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	PartyCustomPingsOverlay partyCustomPingsOverlay;

	@Inject
	KeyManager keyManager;

	private Keybind emoteWheelHotkey;

	protected long wheelOpened = -1;
	protected Point screenPoint = null;
	protected Tile tilePoint = null;

	@Override
	protected void startUp()
	{
		overlayManager.add(partyCustomPingsOverlay);
		updateConfig();
		keyManager.registerKeyListener(EmoteWheelHotkeyListener);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(partyCustomPingsOverlay);
		keyManager.unregisterKeyListener(EmoteWheelHotkeyListener);
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(CONFIG_GROUP))
		{
			return;
		}

		updateConfig();
	}

	private void updateConfig()
	{
		keyManager.unregisterKeyListener(EmoteWheelHotkeyListener);
		emoteWheelHotkey = config.emoteWheelKey();
		keyManager.registerKeyListener(EmoteWheelHotkeyListener);
	}

	@Provides
	PartyCustomPingsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PartyCustomPingsConfig.class);
	}

	private final HotkeyListener EmoteWheelHotkeyListener = new HotkeyListener(() -> this.emoteWheelHotkey)
	{
		@Override
		public void hotkeyPressed()
		{
			//set point before time in case renderer sees wheel opened first before point
			screenPoint = client.getMouseCanvasPosition();
			tilePoint = client.getSelectedSceneTile();
			wheelOpened = System.currentTimeMillis();


			if (tilePoint == null)
			{

				return;
			}



			boolean isOnCanvas = false;
			for (MenuEntry menuEntry : client.getMenuEntries())
			{
				if (menuEntry == null)
				{
					continue;
				}

				if ("Walk here".equals(menuEntry.getOption()))
				{

					isOnCanvas = true;
					break;
				}
			}

			if (!isOnCanvas)
			{

				return;
			}

			calcSegments(8);
		}

		@Override
		public void hotkeyReleased()
		{

			wheelOpened = -1;
			tilePoint = null;
			screenPoint = null;
		}
	};

	private void calcSegments(int segments)
	{

	}

	@Subscribe
	public void onFocusChanged(FocusChanged focusChanged)
	{
		if (!focusChanged.isFocused())
		{

			EmoteWheelHotkeyListener.hotkeyReleased();
		}
	}

//	@Subscribe
//	public void onBeforeRender(BeforeRender event) {
//		if (wheelOpened == - 1) {
//			return;
//		}
//
//		Point mousePoint = client.getMouseCanvasPosition();
//
//
//	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{


		if (wheelOpened == -1 || client.isMenuOpen() )
		{

			return;
		}

		if (tilePoint == null)
		{

			return;
		}



		event.consume(); //will cancel event

		//final EmotePing tilePing = new TilePing(tilePoint.getWorldLocation());
		//party.send(tilePing);
	}

	/*@Subscribe
	public void onTilePing(TilePing event)
	{
		if (config.pings())
		{
			final PartyData partyData = getPartyData(event.getMemberId());
			final Color color = partyData != null ? partyData.getColor() : Color.RED;
			pendingTilePings.add(new PartyTilePingData(event.getPoint(), color));
		}

		if (config.sounds())
		{
			WorldPoint point = event.getPoint();

			if (point.getPlane() != client.getPlane() || !WorldPoint.isInScene(client, point.getX(), point.getY()))
			{
				return;
			}

			clientThread.invoke(() -> client.playSoundEffect(SoundEffectID.SMITH_ANVIL_TINK));
		}
	}*/
}
