/**
 * ZK support for OSGi environments.
 *
 * Because ZUL component controllers are instantiated by the ZK loader servlet
 * rather than by the OSGi container, SCR annotations don't work.
 * This package reimplements SCR-style dependency injection.
 * Controllers should extend {@link SCRSelectorComposer} and use the
 * workalike {@link Reference} annotation.
 */
package au.id.raboczi.cornerstone.zk;
