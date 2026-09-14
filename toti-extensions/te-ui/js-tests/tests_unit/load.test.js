const {
  parsePathParams,
  parseQueryParams,
  getMethod,
  parseBodyParams,
  parseHeaders
}  = require('../src/load');

var dataset = [
  {
    name: 'parsePathParams',
    method: parsePathParams,
    replaceBraces: false,
    tests: [
      {
        name: 'No path parameter',
        expected: '/original/url',
        data: [
          '/original/url',
          {}
        ]
      },
      {
        name: 'Absolute',
        expected: 'https://toti.com/original/url',
        data: [
          'https://toti.com/original/url',
          { path: [] }
        ]
      },
      {
        name: 'Absolute to example.com',
        expected: 'https://example.com/original/url',
        data: [
          'https://example.com/original/url',
          { path: [] }
        ]
      },
      {
        name: 'No first slash',
        expected: 'original/url',
        data: [
          'original/url',
          { path: [] }
        ]
      },
      {
        name: 'Replace [param]',
        expected: '/original/url/hall/1',
        data: [
          '/original/url/[param]/[param]',
          {
            path: [ 'hall', 1 ]
          }
        ]
      },
      {
        name: 'Add params to end 1',
        expected: '/original/url/hall/1',
        data: [
          '/original/url',
          {
            path: [ 'hall', 1 ]
          }
        ]
      },
      {
        name: 'Add params to end 2',
        expected: '/original/url/hall/1',
        data: [
          '/original/url/',
          {
            path: [ 'hall', 1 ]
          }
        ]
      },
      {
        name: 'Mixed params',
        expected: '/original/url/hall/1',
        data: [
          '/original/url/[param]',
          {
            path: [ 'hall', 1 ]
          }
        ]
      },
      {
        name: 'With query params',
        expected: '/original/url/hall/1?a=A&b=B',
        data: [
          '/original/url/[param]?a=A&b=B',
          {
            path: [ 'hall', 1 ]
          }
        ]
      }
    ]
  },
  {
    name: 'parseQueryParams',
    method: parseQueryParams,
    replaceBraces: true,
    tests: [
      {
        name: 'No param',
        expected: '/original/url',
        data: [
          '/original/url',
          {},
          'someName'
        ]
      },
      {
        name: 'No param - another name',
        expected: '/original/url',
        data: [
          '/original/url',
          {
            query: { a: 'A'},
            someName: { a: 'A'}
          },
          'anotherName'
        ]
      },
      {
        name: 'Append parameters',
        expected: '/original/url?a=A&b=B',
        data: [
          '/original/url',
          {
            query: {
              a: 'A',
              b: 'B'
            }
          },
          'query'
        ]
      },
      {
        name: 'Url contains parameters',
        expected: '/original/url?c=C&a=A&b=B',
        data: [
          '/original/url?c=C',
          {
            query: {
              a: 'A',
              b: 'B'
            }
          },
          'query'
        ]
      },
      {
        name: 'Existing parameter',
        expected: '/original/url?a=X&a=Y',
        data: [
          '/original/url?a=X',
          {
            query: {
              a: 'Y'
            }
          },
          'query'
        ]
      },
      {
        name: 'List',
        expected: '/original/url?c=C&a[]=A1&a[]=A2',
        data: [
          '/original/url?c=C',
          {
            query: {
              a: ['A1', 'A2']
            }
          },
          'query'
        ]
      },
      {
        name: 'Map',
        expected: '/original/url?c=C&a[x]=X&a[y]=Y',
        data: [
          '/original/url?c=C',
          {
            query: {
              a: {
                x: 'X',
                y: 'Y'
              }
            }
          },
          'query'
        ]
      },
      {
        name: 'Map merge',
        expected: '/original/url?a[a]=A&a[x]=X&a[y]=Y',
        data: [
          '/original/url?a[a]=A',
          {
            query: {
              a: {
                x: 'X',
                y: 'Y'
              }
            }
          },
          'query'
        ]
      },
      {
        name: 'List merge',
        expected: '/original/url?a[]=X&a[]=A1&a[]=A2',
        data: [
          '/original/url?a[]=X',
          {
            query: {
              a: ['A1', 'A2']
            }
          },
          'query'
        ]
      }
    ]
  },
  {
    name: 'getMethod',
    method: getMethod,
    replaceBraces: false,
    tests: [
      {
        name: 'Without method param',
        expected: 'GET',
        data: [{}]
      },
      {
        name: 'With Get method param',
        expected: 'Get',
        data: [{
          method: 'Get'
        }]
      },
      {
        name: 'With POST method param',
        expected: 'POST',
        data: [{
          method: 'POST'
        }]
      }
    ]
  },
  // some problem with test
  /* {
    name: 'parseHeaders',
    method: parseHeaders,
    replaceBraces: false,
    tests: [
      {
        name: 'No param',
        expected: new Headers({}),
        data: [
          {}
        ]
      },
      {
        name: 'Simple headers',
        expected: new Headers({ a: 'A', b: 'B'}),
        data: [
          {
            headers: {
              a: 'A',
              b: 'B'
            }
          }
        ]
      },
      {
        name: 'Multilevel',
        expected: new Headers({ a: 'A', b: 'B1, B2'}),
        data: [
          {
            headers: {
              a: 'A',
              b: ['B1','B2']
            }
          }
        ]
      }
    ]
  }, */
  {
    name: 'parseBodyParams',
    method: parseBodyParams,
    replaceBraces: false,
    tests: [
      // TODO
      /*{
        name: '',
        expected: null,
        data: []
      }*/
    ]
  }
];

dataset.forEach((group)=>{
  describe(group.name, ()=>{
    group.tests.forEach((useCase)=>{
      test(useCase.name, ()=>{
        var expected = useCase.expected;
        if (group.replaceBraces) {
          // for better reading
          expected = expected.replaceAll('[', '%5B').replaceAll(']', '%5D');
        }
        var actual = group.method(...useCase.data);
        expect(actual).toBe(expected);
      });
    });
  });
});