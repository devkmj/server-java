import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
	scenarios: {
		peak_test: {
			executor: 'constant-arrival-rate',
			rate: 1000, // 초당 요청 수
			timeUnit: '1s',
			duration: '10s',
			preAllocatedVUs: 1000,
			maxVUs: 2000,
		},
	},
	thresholds: {
		http_req_duration: [
			'avg<300',
			'p(90)<300',
			'p(95)<500',
			'p(99)<1000',
			'max<3000',
		],
		http_req_failed: ['rate<0.01'], // 1% 미만만 실패 허용
	},
};

const BASE_URL = 'http://localhost:8080'; // Spring 서버 주소
const COUPON_ID = 19;

export default function () {
	const userId = Math.floor(Math.random() * 10000) + 1; // 1~10000 랜덤 userId
	const payload = JSON.stringify({
		userId: userId,
		couponId: COUPON_ID
	});

	const headers = { 'Content-Type': 'application/json' };

	let res = http.post(`${BASE_URL}/coupons/issue`, payload, { headers });

	// 실패 응답 로깅
	if (res.status !== 200) {
		console.error(`Fail - userId=${userId}, status=${res.status}, body=${res.body}`);
	}

	check(res, {
		'status is 200 or expected failure': (r) => {
			const data = r.json()?.data;
			return (
				r.status === 200 ||
				data === '이미 발급된 쿠폰입니다.' ||
				data === '쿠폰 재고가 부족합니다.'
			);
		},
	});

	sleep(0.5); // 과도한 요청 방지
}
