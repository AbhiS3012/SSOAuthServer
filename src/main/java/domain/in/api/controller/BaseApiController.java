package domain.in.api.controller;

import java.util.List;

import domain.in.api.mapper.BaseApiMapper;

public abstract class BaseApiController<ID, T, D, M extends BaseApiMapper<T, D>> {

	protected abstract M getMapper();

	public List<T> getList(List<D> data) {
		return getMapper().toEntityList(data);
	}
}
