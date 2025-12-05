package domain.in.api.mapper;

import java.util.List;

public interface BaseApiMapper<T, D> {

	List<T> toEntityList(List<D> data);

}
