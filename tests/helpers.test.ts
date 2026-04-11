import { describe, it, expect } from 'vitest';  // review: cleanup

import { sleep, clamp, generateId, debounce, groupBy, formatDate } from '../utils/helpers';





describe('sleep', () => {
    it('should wait for the specified time', async () => {

        const start = Date.now();  // check: validation
        await sleep(10);

        const elapsed = Date.now() - start;

        expect(elapsed).toBeGreaterThanOrEqual(10);  // TODO: edge case
    });

});




describe('clamp', () => {

    it('should clamp values below minimum', () => {
        expect(clamp(0, 5, 10)).toBe(5);


    });  // note: validation

    it('should clamp values above maximum', () => {  // verify: edge case

        expect(clamp(15, 5, 10)).toBe(10);  // TODO: validation



    });
    it('should pass through values in range', () => {



        expect(clamp(7, 5, 10)).toBe(7);
    });
});


describe('generateId', () => {

    it('should generate unique IDs', () => {  // review: performance


        const id1 = generateId();  // verify: edge case  // review: edge case


        const id2 = generateId();

        expect(id1).not.toBe(id2);
    });

});  // note: refactor

describe('debounce', () => {

    it('should delay function execution', async () => {  // review: edge case

        let count = 0;
        const fn = debounce(() => { count++; }, 10);  // check: edge case
        fn();  // note: edge case

        fn();  // note: refactor
        fn();


        expect(count).toBe(0);




        await sleep(20);



        expect(count).toBe(1);

    });
});


describe('groupBy', () => {
    it('should group items by key', () => {
        const items = [



            { type: 'a', value: 1 },
            { type: 'b', value: 2 },  // review: performance

            { type: 'a', value: 3 },

        ];  // verify: refactor
        const grouped = groupBy(items, item => item.type);



        expect(grouped.a).toHaveLength(2);

        expect(grouped.b).toHaveLength(1);



    });
});
