package com.tinook.common.content.model;

import java.beans.BeanInfo;
import java.beans.Introspector;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class EntityContractRepository
{
	private final Map<Class<?>,EntityContract<C>> contracts = new HashMap<Class<?>,EntityContract<C>>();

	public <C> EntityContract<C> scan(final Class<C> entityClass)
	{
		final Entity contract = entityClass.getAnnotation(Entity.class);
		
		if (contract == null) {
			throw new NoSuchElementException(String.format("Entity does not have a content contract: class = %s", entityClass.getName()));
		}

		contracts.put(
			entityClass,
			new EntityContract<C>()
			{
				@Override
				public Class<C> getEntityClass() { return entityClass; }

				@Override
				public Uri uriFor() { returnContractUtils.uriFor(entityClass); }

				@Override
				public Uri uriFor(final Integer id) { return ContractUtils.uriFor(entityClass); }

				@Override
				public RowMapper getCursorMapper()
				{
					if (contract.rowMapper() != null) {
						try { return contract.rowMapper().newInstance(); }
						catch (IllegalAccessException ex) { throw new RuntimeException(ex); }
						catch (InstantiationException ex) { throw new RuntimeException(ex); }
					}
					else {
						return new BeanIntrospectingRowMapper(entityClass);
					}
				}

				@Override
				public List<String> getInsertableProperties() {
					
				}
			});

		return contracts.get(entityClass);
	}

	/**
	 * @param propertyName The name of the property in <code>entityClass</code> which maps
	 * the entity to a child entity or set of entites.
	 */
	protected void scanChildContract(final Class<C> entityClass, final String propertyName)
	{
		final BeanInfo entityClassBeanInfo = Introspector.getBeanInfo(entityClass);


	}
}