package tw.mmunki.d8wrapper.service;

import tw.mmunki.d8wrapper.exception.WrapperException;
import tw.mmunki.d8wrapper.model.CompilationReport;
import tw.mmunki.d8wrapper.model.DexRequest;

/** Compiles class files into DEX files. */
public interface DexCompiler {

  /**
   * Compiles the inputs of a request into DEX files.
   *
   * @param request the request to compile
   * @return the report of the compilation, it tells whether the compilation succeeded
   * @throws WrapperException when the compilation cannot be started at all
   */
  CompilationReport compile(DexRequest request) throws WrapperException;
}
