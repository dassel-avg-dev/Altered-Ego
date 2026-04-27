package util;

import entity.Entity;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Allocates and caches one {@link Entity} instance per {@link GameCharacter}.
 */
public class EntityPool {
    private final Map<GameCharacter, Entity> pool = new EnumMap<>(GameCharacter.class);

    /**
     * Constructs the pool by creating one {@link Entity} for every
     * {@link GameCharacter} value.
     */
    public EntityPool() {
        for (GameCharacter gc : GameCharacter.values()) {
            pool.put(gc, new Entity(
                    gc.getName(),
                    gc.getSkill1Name(),
                    gc.getSkill2Name(),
                    gc.getSkill3Name()
            ));
        }
    }

    /**
     * Returns the {@link Entity} associated with the given character.
     *
     * @param gameCharacter the {@link GameCharacter} of the entity
     * @return the corresponding {@link Entity}
     */
    public Entity get(GameCharacter gameCharacter) {
        return pool.get(gameCharacter);
    }

    public int size() {
        return pool.size();
    }

    public Set<GameCharacter> keySet() {
        return pool.keySet();
    }
}
