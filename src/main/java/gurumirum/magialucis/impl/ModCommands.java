package gurumirum.magialucis.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import gurumirum.magialucis.api.MagiaLucisApi;
import gurumirum.magialucis.api.field.Field;
import gurumirum.magialucis.api.field.FieldRegistry;
import gurumirum.magialucis.api.luxnet.LinkInfo;
import gurumirum.magialucis.api.luxnet.LuxNodeInterface;
import gurumirum.magialucis.impl.field.FieldManager;
import gurumirum.magialucis.impl.field.ServerFieldInstance;
import gurumirum.magialucis.impl.luxnet.ServerLuxNet;
import gurumirum.magialucis.impl.luxnet.ServerLuxNode;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.core.BlockPos;
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
import java.util.Objects;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber(modid = MagiaLucisApi.MODID)
public final class ModCommands {
	private ModCommands() {}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(literal(MagiaLucisApi.MODID)
				.then(literal("luxnet")
						.then(literal("print")
								.requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
								.then(literal("nodes")
										.executes(context ->
												printLuxNetNodes(context.getSource(), context.getSource().getLevel())
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														printLuxNetNodes(context.getSource(),
																DimensionArgument.getDimension(context, "dimension")))
										)
								).then(literal("outboundLinks")
										.executes(context ->
												printLuxNetLinks(context.getSource(), context.getSource().getLevel(), true)
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														printLuxNetLinks(context.getSource(),
																DimensionArgument.getDimension(context, "dimension"),
																true))
										)
								).then(literal("inboundLinks")
										.executes(context ->
												printLuxNetLinks(context.getSource(), context.getSource().getLevel(), false)
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														printLuxNetLinks(context.getSource(),
																DimensionArgument.getDimension(context, "dimension"),
																false))
										)
								).then(literal("node")
										.then(argument("nodeId", IntegerArgumentType.integer())
												.executes(context ->
														printLuxNetNode(context.getSource(),
																context.getSource().getLevel(),
																IntegerArgumentType.getInteger(context, "nodeId"))
												).then(argument("dimension", DimensionArgument.dimension())
														.executes(context ->
																printLuxNetNode(context.getSource(),
																		DimensionArgument.getDimension(context, "dimension"),
																		IntegerArgumentType.getInteger(context, "nodeId")))
												)
										)
								)
						).then(literal("clear")
								.requires(stack -> stack.hasPermission(Commands.LEVEL_GAMEMASTERS))
								.then(literal("all")
										.executes(context ->
												clearLuxNet(context.getSource(), context.getSource().getLevel(), ServerLuxNet.ClearMode.ALL)
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														clearLuxNet(context.getSource(),
																DimensionArgument.getDimension(context, "dimension"),
																ServerLuxNet.ClearMode.ALL))
										)
								).then(literal("unloaded")
										.executes(context ->
												clearLuxNet(context.getSource(), context.getSource().getLevel(), ServerLuxNet.ClearMode.UNLOADED)
										).then(argument("dimension", DimensionArgument.dimension())
												.executes(context ->
														clearLuxNet(context.getSource(),
																DimensionArgument.getDimension(context, "dimension"),
																ServerLuxNet.ClearMode.UNLOADED))
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
		ServerLuxNet luxNet = ServerLuxNet.get(level);

		Int2ObjectMap<ServerLuxNode> nodes = luxNet.nodes();

		source.sendSuccess(() -> Component.literal("Luxnet of dimension " + level.dimension().location() + ": " +
				nodes.size() + " nodes"), false);

		if (!nodes.isEmpty()) {
			MutableComponent c = Component.empty();
			boolean _first = true;

			for (var e : nodes.int2ObjectEntrySet()) {
				if (_first) _first = false;
				else c.append("  ");

				c.append(node(luxNet, e.getValue()));
			}

			source.sendSuccess(() -> c, false);
		}

		return 1;
	}

	private static int printLuxNetNode(CommandSourceStack source, ServerLevel level, int nodeId) {
		ServerLuxNet luxNet = ServerLuxNet.get(level);
		ServerLuxNode node = luxNet.get(nodeId);
		if (node == null) {
			source.sendFailure(Component.literal("No luxnet node with ID " + nodeId));
			return 0;
		} else {
			MutableComponent c = Component.empty();
			boolean first = true;

			for (String s : nodeDescription(luxNet, node)) {
				if (first) first = false;
				else c.append("\n");
				c.append(s);
			}

			source.sendSuccess(() -> c, false);
			return 1;
		}
	}

	private static MutableComponent node(ServerLuxNet luxNet, ServerLuxNode node) {
		return Component.literal(node.toString()).withStyle(nodeTooltip(luxNet, node));
	}

	private static Style nodeTooltip(ServerLuxNet luxNet, ServerLuxNode node) {
		return Style.EMPTY.withHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(
						String.join("\n", nodeDescription(luxNet, node)))));
	}

	private static List<String> nodeDescription(ServerLuxNet luxNet, ServerLuxNode node) {
		List<String> tooltips = new ArrayList<>();

		ResourceLocation id = node.behavior().type().id();
		tooltips.add("Node #" + node.id() + ": " + (id.getNamespace().equals(MagiaLucisApi.MODID) ? id.getPath() : id.toString()));

		BlockPos pos = node.lastBlockPos();
		tooltips.add("Block Position: " + (pos == null ? "N/A" :
				"(" + pos.toShortString() + ")" + (node.isLoaded() ? "" : "?")));

		LuxNodeInterface iface = node.iface();
		if (iface != null) {
			tooltips.add("Interface: " + iface.getClass().getSimpleName() + " " + Integer.toHexString(System.identityHashCode(iface)));
		}

		if (luxNet.hasInboundLink(node.id())) {
			tooltips.add("Inbound Links:");
			for (var e2 : Objects.requireNonNull(luxNet.inboundLinks(node.id())).links().entrySet()) {
				LinkInfo info = e2.getValue();
				String str = " " + e2.getKey();
				if (info.inWorld() == null) str += " implicit";
				tooltips.add(str);
			}
		}

		if (luxNet.hasOutboundLink(node.id())) {
			tooltips.add("Outbound Links:");
			for (var e2 : Objects.requireNonNull(luxNet.outboundLinks(node.id())).links().entrySet()) {
				LinkInfo info = e2.getValue();
				String str = " " + e2.getKey();
				if (info.inWorld() == null) str += " implicit";
				tooltips.add(str);
			}
		}

		return tooltips;
	}

	private static int printLuxNetLinks(CommandSourceStack source, ServerLevel level, boolean outbound) {
		ServerLuxNet luxNet = ServerLuxNet.get(level);
		IntSet nodesWithLink = outbound ? luxNet.nodesWithOutboundLink() : luxNet.nodesWithInboundLink();
		int links = nodesWithLink.intStream()
				.map(n -> (Objects.requireNonNull(outbound ? luxNet.outboundLinks(n) : luxNet.inboundLinks(n)))
						.links().size())
				.sum();

		source.sendSuccess(() -> Component.literal("LuxNet of dimension " + level.dimension().location() + ": " +
				links + " links"), false);

		if (links > 0) {
			nodesWithLink.forEach(id -> {
				ServerLuxNode node = Objects.requireNonNull(luxNet.get(id));
				var map = Objects.requireNonNull(outbound ? luxNet.outboundLinks(id) : luxNet.inboundLinks(id));
				if (map.links().isEmpty()) return;

				MutableComponent nodeText = node(luxNet, node);
				MutableComponent things = Component.empty();

				things.append(outbound ? " -> { " : "{ ");

				boolean first = true;

				for (var e : map.links().entrySet()) {
					if (first) first = false;
					else things.append(", ");

					things.append(Component.literal(
							e.getValue().inWorld() == null ? "*" + e.getKey() + "*" : "" + e.getKey()
					).withStyle(nodeTooltip(luxNet, e.getKey())));
				}

				things.append(outbound ? " }" : " } -> ");

				source.sendSuccess(() -> outbound ?
						Component.empty().append(nodeText).append(things) :
						Component.empty().append(things).append(nodeText), false);
			});
		}

		return 1;
	}

	private static int clearLuxNet(CommandSourceStack source, ServerLevel level, ServerLuxNet.ClearMode clearMode) {
		ServerLuxNet.get(level).clear(clearMode);
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
		ServerFieldInstance fieldInstance = manager.get(field);
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
