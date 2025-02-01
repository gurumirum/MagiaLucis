package gurumirum.magialucis.impl;

import gurumirum.magialucis.MagiaLucisMod;
import gurumirum.magialucis.impl.field.Field;
import gurumirum.magialucis.impl.field.FieldInstance;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.FieldRegistry;
import gurumirum.magialucis.impl.luxnet.InWorldLinkInfo;
import gurumirum.magialucis.impl.luxnet.LuxNet;
import gurumirum.magialucis.impl.luxnet.LuxNode;
import gurumirum.magialucis.impl.luxnet.LuxNodeInterface;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber(modid = MagiaLucisMod.MODID)
public final class ModCommands {
	private ModCommands() {}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(literal(MagiaLucisMod.MODID)
				.then(literal("luxnet")
						.then(literal("print")
								.requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
								.then(literal("nodes")
										.executes(context ->
												printLuxNetNodes(context.getSource(), context.getSource().getLevel())
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														printLuxNetNodes(context.getSource(), DimensionArgument.getDimension(context, "dimension")))
										)
								).then(literal("links")
										.executes(context ->
												printLuxNetLinks(context.getSource(), context.getSource().getLevel())
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														printLuxNetLinks(context.getSource(), DimensionArgument.getDimension(context, "dimension")))
										)
								)
						).then(literal("clear")
								.requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
								.then(literal("all")
										.executes(context ->
												clearLuxNet(context.getSource(), context.getSource().getLevel(), LuxNet.ClearMode.ALL)
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														clearLuxNet(context.getSource(), DimensionArgument.getDimension(context, "dimension"), LuxNet.ClearMode.ALL))
										)
								).then(literal("unloaded")
										.executes(context ->
												clearLuxNet(context.getSource(), context.getSource().getLevel(), LuxNet.ClearMode.UNLOADED)
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														clearLuxNet(context.getSource(), DimensionArgument.getDimension(context, "dimension"), LuxNet.ClearMode.UNLOADED))
										)
								)
						)
				).then(literal("field")
						.requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
						.then(argument("fieldId", ResourceLocationArgument.id())
								.then(literal("print")
										.executes(context ->
												printField(context.getSource(),
														context.getSource().getLevel(),
														ResourceLocationArgument.getId(context, "fieldId")))
										.then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														printField(context.getSource(),
																DimensionArgument.getDimension(context, "dimension"),
																ResourceLocationArgument.getId(context, "fieldId")))
										)
								)
						)
				)
		);
	}

	private static int printLuxNetNodes(CommandSourceStack source, ServerLevel level) {
		LuxNet luxNet = LuxNet.get(level);

		Int2ObjectMap<LuxNode> nodes = luxNet.nodes();

		source.sendSuccess(() -> Component.literal("Luxnet of dimension " + level.dimension().location() + ": " +
				nodes.size() + " nodes"), false);

		MutableComponent c = Component.empty();
		boolean _first = true;

		for (var e : nodes.int2ObjectEntrySet()) {
			if (_first) _first = false;
			else c.append("  ");

			c.append(node(luxNet, e.getValue()));
		}

		source.sendSuccess(() -> c, false);

		return 1;
	}

	private static MutableComponent node(LuxNet luxNet, LuxNode node) {
		MutableComponent c = Component.literal(node.toString());
		List<String> tooltips = new ArrayList<>();

		LuxNodeInterface iface = node.iface();
		if (iface != null) {
			tooltips.add("Interface: " + iface);
		}

		if (luxNet.hasInboundLink(node)) {
			tooltips.add("Inbound Links:");
			for (var e2 : luxNet.inboundLinks(node).entrySet()) {
				InWorldLinkInfo info = e2.getValue();
				String str = " " + e2.getKey();
				if (info == null) str += " implicit";
				tooltips.add(str);
			}
		}

		if (luxNet.hasOutboundLink(node)) {
			tooltips.add("Outbound Links:");
			for (var e2 : luxNet.outboundLinks(node).entrySet()) {
				InWorldLinkInfo info = e2.getValue();
				String str = " " + e2.getKey();
				if (info == null) str += " implicit";
				tooltips.add(str);
			}
		}

		if (!tooltips.isEmpty()) {
			c = c.withStyle(Style.EMPTY.withHoverEvent(
					new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(
							String.join("\n", tooltips)
					))
			));
		}

		return c;
	}

	private static int printLuxNetLinks(CommandSourceStack source, ServerLevel level) {
		LuxNet luxNet = LuxNet.get(level);

		Set<LuxNode> nodesWithOutboundLink = luxNet.nodesWithOutboundLink();
		Set<LuxNode> nodesWithInboundLink = luxNet.nodesWithInboundLink();

		int links = nodesWithOutboundLink.stream()
				.mapToInt(n -> luxNet.outboundLinks(n).size())
				.sum();

		source.sendSuccess(() -> Component.literal("Luxnet of dimension " + level.dimension().location() + ": " +
				links + " links"), false);

		source.sendSuccess(() -> Component.literal("Outbound Links: "), false);

		for (LuxNode node : nodesWithOutboundLink) {
			var map = luxNet.outboundLinks(node);
			if (map.isEmpty()) continue;
			source.sendSuccess(() -> Component.literal(node.id + " -> [" +
					map.entrySet().stream()
							.map(e -> e.getValue() == null ? "(" + e.getKey().id + ")" : "" + e.getKey().id)
							.collect(Collectors.joining(", ")) + "]"), false);
		}

		source.sendSuccess(() -> Component.literal("Inbound Links: "), false);

		for (LuxNode node : nodesWithInboundLink) {
			var map = luxNet.inboundLinks(node);
			if (map.isEmpty()) continue;
			source.sendSuccess(() -> Component.literal("[" +
					map.entrySet().stream()
							.map(e -> e.getValue() == null ? "(" + e.getKey().id + ")" : "" + e.getKey().id)
							.collect(Collectors.joining(", ")) + "] -> " + node.id), false);
		}

		return 1;
	}

	private static int clearLuxNet(CommandSourceStack source, ServerLevel level, LuxNet.ClearMode clearMode) {
		LuxNet.get(level).clear(clearMode);
		source.sendSuccess(() -> Component.literal("Cleared lux network configuration in " + level.dimension().location() + "."), true); // TODO localize
		return 1;
	}

	private static int printField(CommandSourceStack source, ServerLevel level, ResourceLocation fieldId) {
		Field field = FieldRegistry.fields().get(fieldId);
		if (field == null) {
			source.sendFailure(Component.literal("Field with id " + fieldId + " does not exist"));
			return 0;
		}

		FieldManager manager = FieldManager.get(level);
		FieldInstance fieldInstance = manager.get(field);
		if (fieldInstance == null) {
			source.sendSuccess(() -> Component.literal("Field " + fieldId + ": not initialized"), false);
		} else {
			source.sendSuccess(() -> Component.literal("Field " + fieldId + ": " + fieldInstance.elements().size() + " elements"), false);
			for (var e : fieldInstance.elements().entrySet()) {
				source.sendSuccess(() -> Component.literal("[" + e.getKey().toShortString() + "]"), false);
			}
		}

		return 1;
	}
}
