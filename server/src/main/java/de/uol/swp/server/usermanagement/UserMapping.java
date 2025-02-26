package de.uol.swp.server.usermanagement;

import de.uol.swp.server.model.UserDTO;
import org.mapstruct.Mapper;

/**
 * Maps the UserDTO to the ServerUser and vice versa.
 * The implementation is done by MapStruct.
 *
 * @author Tilman Holube
 * @see UserDTO
 * @see ServerUser
 * @since 2025-03-17
 */
@Mapper(componentModel = "spring")
public interface UserMapping {

    /**
     * Maps a ServerUser to a UserDTO
     *
     * @param user The ServerUser to map
     * @return The mapped UserDTO
     * @since 2025-03-17
     */
    UserDTO toDTO(ServerUser user);

}
