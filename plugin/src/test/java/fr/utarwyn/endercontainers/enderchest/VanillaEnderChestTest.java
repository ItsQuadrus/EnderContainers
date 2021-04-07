package fr.utarwyn.endercontainers.enderchest;

import fr.utarwyn.endercontainers.TestHelper;
import fr.utarwyn.endercontainers.TestInitializationException;
import fr.utarwyn.endercontainers.enderchest.context.PlayerContext;
import fr.utarwyn.endercontainers.enderchest.context.PlayerOfflineLoadException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VanillaEnderChestTest {

    private VanillaEnderChest chest;

    private Player player;

    @Mock
    private PlayerContext context;

    @Mock
    private Inventory inventory;

    @BeforeClass
    public static void setUpClass() throws TestInitializationException {
        TestHelper.setUpFiles();
    }

    @Before
    public void setUp() throws PlayerOfflineLoadException {
        this.player = TestHelper.getPlayer();

        when(this.player.getEnderChest()).thenReturn(inventory);
        when(this.context.getOwnerAsObject()).thenReturn(this.player);

        this.chest = new VanillaEnderChest(this.context);
    }

    @Test
    public void create() {
        assertThat(this.chest.getNum()).isZero();
        assertThat(this.chest.getRows()).isEqualTo(3);
        assertThat(this.chest.getOwnerAsPlayer()).isEqualTo(this.player);
    }

    @Test
    public void isContainerUsed() {
        // No viewer
        when(this.inventory.getViewers()).thenReturn(Collections.emptyList());
        assertThat(this.chest.isContainerUsed()).isFalse();

        // At least one viewer
        when(this.inventory.getViewers()).thenReturn(Collections.singletonList(mock(Player.class)));
        assertThat(this.chest.isContainerUsed()).isTrue();
    }

    @Test
    public void isUsedBy() {
        Player viewer = mock(Player.class);
        when(this.inventory.getViewers()).thenReturn(Collections.singletonList(viewer));

        assertThat(this.chest.isUsedBy(viewer)).isTrue();
        assertThat(this.chest.isUsedBy(mock(Player.class))).isFalse();
    }

    @Test
    public void getSize() {
        ItemStack item = mock(ItemStack.class);
        when(this.inventory.getContents()).thenReturn(
                Arrays.asList(item, null, item, item, null).toArray(new ItemStack[0])
        );
        assertThat(this.chest.getSize()).isEqualTo(3);
    }

    @Test
    public void openContainerFor() {
        this.chest.openContainerFor(this.player);
        verify(this.player).openInventory(this.inventory);
    }

    @Test
    public void unknownPlayer() {
        when(this.context.getOwnerAsObject()).thenReturn(null);
        this.chest = new VanillaEnderChest(this.context);

        try {
            this.chest.loadOfflinePlayer();
        } catch (PlayerOfflineLoadException ignored) {
        }

        assertThat(this.chest.isContainerUsed()).isFalse();
        assertThat(this.chest.getSize()).isZero();

        // do nothing when trying to open a container if owner is not defined
        this.chest.openContainerFor(this.player);
        verify(this.player, never()).openInventory(this.inventory);
    }

}
