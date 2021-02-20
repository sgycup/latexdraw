/*
 * This file is part of LaTeXDraw.
 * Copyright (c) 2005-2020 Arnaud BLOUIN
 * LaTeXDraw is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * LaTeXDraw is distributed without any warranty; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 */
package net.sf.latexdraw.instrument;

import io.github.interacto.command.CommandsRegistry;
import java.util.Optional;
import net.sf.latexdraw.command.shape.CopyShapes;
import net.sf.latexdraw.command.shape.SelectShapes;
import org.jetbrains.annotations.NotNull;

/**
 * A helper to search in the command registry.
 * @author  Arnaud Blouin
 */
public interface CmdRegistrySearcher {
	default @NotNull Optional<CopyShapes> getCopyCutCmd() {
		return CommandsRegistry.getInstance().getCommands().parallelStream().
			filter(cmd -> cmd instanceof CopyShapes).map(cmd -> (CopyShapes) cmd).findFirst();
	}

	default @NotNull Optional<SelectShapes> getSelectCmd() {
		return CommandsRegistry.getInstance().getCommands().parallelStream().
			filter(cmd -> cmd instanceof SelectShapes).map(cmd -> (SelectShapes) cmd).findFirst();
	}
}
