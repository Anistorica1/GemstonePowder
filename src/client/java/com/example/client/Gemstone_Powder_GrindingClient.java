package com.example.client;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.util.HashMap;
import java.util.Map;

public class Gemstone_Powder_GrindingClient implements ClientModInitializer {
	public static KeyMapping gemstonePowder = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.gemstone.powder",
			GLFW.GLFW_KEY_F,
			Category.MISC
	));
	public static boolean running = false;
	Minecraft client = Minecraft.getInstance();
	int tickCounter,phase,counter;
	boolean cropfirst,markFirst;
	private double tempx = 0, tempz = 0;
	private static Map<String, Integer> dict = new HashMap<String, Integer>();
	static {
		dict.put("netherwarts", 0);
		dict.put("wheat", 1);
		dict.put("test", 2);
		dict.put("wildrose",3);
		dict.put("cactus", 4);
		dict.put("mushroom", 5);
		dict.put("sugarcane", 6);
		dict.put("potatoes", 7);
	}
	private String mode = "netherwarts";

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) return;
			while (gemstonePowder.consumeClick()) {
				running = !running;
				tickCounter = 0;
				phase = 0;
				resetKeys(client);
				client.player.sendSystemMessage(
						Component.literal(running ? "§aGemstonePowderModeIsEnabled" : "§cGemstonePowderModeIsDisabled")
				);
//				client.player.sendSystemMessage(
//						Component.literal(client.player.getX()+" ")
//				);
//				client.player.connection.sendCommand("/hello");
			}


			// 切换时立即设置按键状态
//				if (running) {
//					client.options.keyUp.setDown(true);
//				} else {
//					client.options.keyUp.setDown(false);
//				}
			if(!running) return;
			tickCounter++;
			HitResult hit = client.hitResult;

			if (phase == 0 && hit != null && hit.getType() == HitResult.Type.BLOCK) {
				BlockHitResult blockHit = (BlockHitResult) hit;
				BlockPos blockPos = blockHit.getBlockPos();
				BlockState blockState = client.level.getBlockState(blockPos);
				Block block = blockState.getBlock();
				if (block.getName().toString().equalsIgnoreCase("translation{key='block.minecraft.chest', args=[]}")) {
					phase = 1;
					tickCounter = 0;
				}
//				System.out.println(block.getName().toString());
			}
			switch (phase) {
				case 0:
					press(client.options.keyAttack);
					break;
				case 1:
					release(client.options.keyAttack);
					if(tickCounter == 1) press(client.options.keyUse);
					if (tickCounter == 3) release(client.options.keyUse);
					if(tickCounter == 4) phase = 0;

			}
		});
	}
	public static void resetKeys(Minecraft client) {
		client.options.keyUp.setDown(false);
		client.options.keyLeft.setDown(false);
		client.options.keyRight.setDown(false);
		client.options.keyDown.setDown(false);
		client.options.keyShift.setDown(false);
		client.options.keyAttack.setDown(false);
	}
	public static void press(KeyMapping keyMapping) {
		keyMapping.setDown(true);
	}
	public static void release(KeyMapping keyMapping) {
		keyMapping.setDown(false);
	}

}
