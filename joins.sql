// 2
SELECT countQuery.aggregate_id
FROM (
	SELECT aggregate_id, COUNT(aggregate_id) AS count
	FROM dimension_combination
	GROUP BY aggregate_id
) countQuery

JOIN (
	SELECT dc1.aggregate_id
	FROM dimension_combination dc1
	LEFT OUTER JOIN dimension_combination dc2
	ON dc1.aggregate_id=dc2.aggregate_id
	WHERE dc1.component_id=1 AND dc2.component_id=7
) sq0
ON countQuery.aggregate_id=sq0.aggregate_id

WHERE countQuery.count=2


// 3
SELECT countQuery.aggregate_id
FROM (
	SELECT aggregate_id, COUNT(aggregate_id) AS count
	FROM dimension_combination
	GROUP BY aggregate_id
) countQuery

JOIN (
	SELECT sq1.aggregate_id
	FROM (
		SELECT dc1.aggregate_id, dc1.component_id as comp1, dc2.component_id as comp2
		FROM dimension_combination dc1
		LEFT OUTER JOIN dimension_combination dc2
		ON dc1.aggregate_id=dc2.aggregate_id
	) sq1
	LEFT OUTER JOIN dimension_combination dc3
	ON sq1.aggregate_id=dc3.aggregate_id
	WHERE sq1.comp1=1 AND sq1.comp2=7 AND dc3.component_id=9
) sq0
ON countQuery.aggregate_id=sq0.aggregate_id

WHERE countQuery.count=3