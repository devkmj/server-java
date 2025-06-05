import http from 'k6/http';
import { check } from 'k6';

export let options = {
	stages: [
		{ duration: '10s', target: 10 },
		{ duration: '30s', target: 20 },
		{ duration: '30s', target: 50 },
		{ duration: '10s', target: 0 },
	],
	thresholds: {
		http_req_duration: [
			'avg<500',
			'p(90)<300',
			'p(95)<500',
			'p(99)<1000',
			'max<3000',
		],
		http_req_failed: ['rate<0.01'],  // 비즈니스 실패 제외 필요시 check로 처리
	},
};

export default function () {
	// 1 ~ 10000 사이의 유저
	const userId = Math.floor(Math.random() * 10000) + 1;

	// 상품 개수 (1 ~ 3개 랜덤 선택)
	const itemCount = Math.floor(Math.random() * 3) + 1;
	const items = Array.from({ length: itemCount }, () => {
		return {
			productId: Math.floor(Math.random() * 20) + 1,  // 1 ~ 20번 상품
			qty: Math.floor(Math.random() * 2) + 1          // 수량: 1~2개
		};
	});

	// // 쿠폰은 30% 확률로 포함, ID는 1~21 범위 (있는 쿠폰 ID만)
	// const couponIncluded = Math.random() < 0.3;
	// const userCouponIds = couponIncluded
	// 	? [Math.floor(Math.random() * 21) + 1]
	// 	: [];

	const payload = JSON.stringify({
		userId: userId,
		items: items,
		userCouponIds: []
	});

	const headers = { 'Content-Type': 'application/json' };

	const res = http.post('http://localhost:8080/orders', payload, { headers });

	// 실패 응답 로깅
	if (res.status !== 200) {
		console.error(`Fail - userId=${userId}, status=${res.status}, body=${res.body}`);
	}

	check(res, {
		'status is 200 or expected failure': (r) =>
			r.status === 200 ||
			r.json()?.message === '재고 부족' ||
			r.json()?.message === '잔액 부족' ||
			r.json()?.message === '쿠폰 유효하지 않음',
	});
}
