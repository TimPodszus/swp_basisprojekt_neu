package de.uol.swp.client;

import de.uol.swp.client.user.ClientUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is the base for creating a new Presenter.
 * <p>
 * This class prepares the child classes to have the UserService available.
 *
 * @author Tilman Holube
 * @since 2025-03-17
 */
@Slf4j
@RequiredArgsConstructor
public class AbstractPresenter {

    protected final ClientUserService userService;

}
