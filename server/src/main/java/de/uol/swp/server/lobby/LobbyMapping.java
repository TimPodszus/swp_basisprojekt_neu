package de.uol.swp.server.lobby;

import de.uol.swp.server.model.LobbyDTO;
import de.uol.swp.server.usermanagement.UserMapping;
import org.mapstruct.Mapper;

/**
 * Maps the LobbyDTO to the ServerLobby and vice versa.
 * The implementation is done by MapStruct.
 * It uses the UserMapping to map the ServerUser to the UserDTO.
 *
 * @author Tilman Holube
 * @see LobbyDTO
 * @see ServerLobby
 * @see UserMapping
 * @since 2025-03-17
 */
@Mapper(componentModel = "spring", uses = {UserMapping.class})
public interface LobbyMapping {

    /**
     * Maps a ServerLobby to a LobbyDTO
     *
     * @param lobby The ServerLobby to map
     * @return The mapped LobbyDTO
     * @since 2025-03-17
     */
    LobbyDTO toDTO(ServerLobby lobby);

}
