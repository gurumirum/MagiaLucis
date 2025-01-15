package gurumirum.magialucis.contents.item.wand;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class WandAttributes {
	private WandAttributes() {}

	private static final ItemAttributeModifiers wand = ItemAttributeModifiers.builder()
			.add(Attributes.ATTACK_DAMAGE,
					new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 1, Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ATTACK_SPEED,
					new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2, Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.build();

	private static final ItemAttributeModifiers staff = ItemAttributeModifiers.builder()
			.add(Attributes.ATTACK_DAMAGE,
					new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 3, Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ATTACK_SPEED,
					new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -3, Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.build();

	private static final ItemAttributeModifiers diamondMace = ItemAttributeModifiers.builder()
			.add(Attributes.ATTACK_DAMAGE,
					new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 7, Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.add(Attributes.ATTACK_SPEED,
					new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2, Operation.ADD_VALUE),
					EquipmentSlotGroup.MAINHAND)
			.build();

	public static ItemAttributeModifiers wand() {
		return wand;
	}

	public static ItemAttributeModifiers staff() {
		return staff;
	}

	public static ItemAttributeModifiers diamondMace() {
		return diamondMace;
	}
}
