const {createInput, createDatetime, timestamp}  = require('../src/inputs');

/**** Input.popupWL ****/
[
  {
    message: 'Zeros',
    screenIW: 0,
    screenOW: 0,
    inputLeft: 0,
    inputWidth: 0,
    requiredWidth: 0,
    expected: {
      width: 0,
      left: 0
    }
  },
  {
    message: 'Small width A',
    screenIW: 1000,
    screenOW: 30,
    inputLeft: 10,
    inputWidth: 20,
    requiredWidth: 50,
    expected: {
      width: 30,
      left: 0
    }
  },
  {
    message: 'Small width B',
    screenIW: 30,
    screenOW: 1000,
    inputLeft: 10,
    inputWidth: 20,
    requiredWidth: 50,
    expected: {
      width: 30,
      left: 0
    }
  },
  {
    message: 'Base display',
    screenIW: 800,
    screenOW: 900,
    inputLeft: 10,
    inputWidth: 20,
    requiredWidth: 50,
    expected: {
      width: 50,
      left: 10
    }
  },
  {
    message: 'Base display - set width',
    screenIW: 800,
    screenOW: 900,
    inputLeft: 10,
    inputWidth: 50,
    requiredWidth: 20,
    expected: {
      width: 50,
      left: 10
    }
  },
  {
    message: 'Not space in right, but enought to full width',
    screenIW: 800,
    screenOW: 900,
    inputLeft: 700,
    inputWidth: 50,
    requiredWidth: 150,
    expected: {
      width: 150,
      left: 650
    }
  },
  {
    message: 'Not space in right, not full width',
    screenIW: 100,
    screenOW: 900,
    inputLeft: 20,
    inputWidth: 50,
    requiredWidth: 150,
    expected: {
      width: 100,
      left: 0
    }
  },
  {
    message: 'Real situation 1',
    screenIW: 587,
    screenOW: 1104,
    inputLeft: 292,
    inputWidth: 169.60000610351562,
    requiredWidth: 68,
    expected: {
      width: 169.60000610351562,
      left: 292
    }
  }/*,
  // TODO
  {
    message: 'Input out of boundaries',
    screenIW: 100,
    screenOW: 900,
    inputLeft: 200,
    inputWidth: 20,
    requiredWidth: 50,
    expected: {
      width: -1,
      left: -1
    }
  }*/
].forEach((data)=>{
    test('Input.popupWL(): ' + data.message, ()=>{
      var input = getInput();
      expect(input.popupWL(data.screenIW, data.screenOW, data.inputLeft, data.inputWidth, data.requiredWidth)).toStrictEqual(data.expected);
  });
});

/**** Input.popupHT ****/
[
  {
    message: 'Zeros',
    screenIH: 0,
    screenOH: 0,
    inputTop: 0,
    inputHeigth: 0,
    requiredHeight: 0,
    expected: {
      top: 0,
      height: 0
    }
  },
  {
    message: 'Bottom, full height',
    screenIH: 800,
    screenOH: 900,
    inputTop: 50,
    inputHeigth: 10,
    requiredHeight: 100,
    expected: {
      top: 60,
      height: 100
    }
  },
  {
    message: 'Bottom, shorted A',
    screenIH: 250,
    screenOH: 800,
    inputTop: 100,
    inputHeigth: 10,
    requiredHeight: 150,
    expected: {
      top: 110,
      height: 140
    }
  },
  {
    message: 'Bottom, shorted B',
    screenIH: 800,
    screenOH: 250,
    inputTop: 100,
    inputHeigth: 10,
    requiredHeight: 150,
    expected: {
      top: 110,
      height: 140
    }
  },
  {
    message: 'Top, full height A',
    screenIH: 800,
    screenOH: 900,
    inputTop: 700,
    inputHeigth: 10,
    requiredHeight: 100,
    expected: {
      top: 600,
      height: 100
    }
  },
  {
    message: 'Top, full height B',
    screenIH: 900,
    screenOH: 800,
    inputTop: 700,
    inputHeigth: 10,
    requiredHeight: 100,
    expected: {
      top: 600,
      height: 100
    }
  },
  {
    message: 'Top, shorted A',
    screenIH: 200,
    screenOH: 800,
    inputTop: 100,
    inputHeigth: 10,
    requiredHeight: 150,
    expected: {
      top: 0,
      height: 100
    }
  },
  {
    message: 'Top, shorted B',
    screenIH: 800,
    screenOH: 200,
    inputTop: 100,
    inputHeigth: 10,
    requiredHeight: 150,
    expected: {
      top: 0,
      height: 100
    }
  },
  {
    message: 'Real situation 1',
    screenIH: 680,
    screenOH: 808,
    inputTop: 559.7250366210938,
    inputHeigth: 21.200000762939453,
    requiredHeight: 250,
    expected: {
      top: 309.72503662109375,
      height: 250
    }
  }
].forEach((data)=>{
    test('Input.popupHT(): ' + data.message, ()=>{
      var input = getInput();
      expect(input.popupHT(data.screenIH, data.screenOH, data.inputTop, data.inputHeigth, data.requiredHeight)).toStrictEqual(data.expected);
  });
});
/*************************/
function getInput() {
    var dumpF = ()=>null;
    return createInput('', {}, {
      parseValue: dumpF,
      create: dumpF,
      setValue: dumpF,
      setDisabled: dumpF
    });
}
/****** parse datetime *********/

var datetimeParseData = {
  '12:20': {
    week: null,
    date: null,
    month: null,
    time: 'time: Y=null, M=null, D=null, H=12, M=20, S=NaN, N=NaN, W=null',
    datetime: null
  },
  '12:20:45': {
    week: null,
    date: null,
    month: null,
    time: 'time: Y=null, M=null, D=null, H=12, M=20, S=45, N=NaN, W=null',
    datetime: null
  },
  '12:20:45.789': {
    week: null,
    date: null,
    month: null,
    time: 'time: Y=null, M=null, D=null, H=12, M=20, S=45, N=789, W=null',
    datetime: null
  },

  '2025-03-18': {
    week: null,
    date: 'date: Y=2025, M=3, D=18, H=null, M=null, S=null, N=null, W=null',
    month: null,
    time: null,
    datetime: null
  },
  '2025-W18': {
    week: 'week: Y=2025, M=null, D=null, H=null, M=null, S=null, N=null, W=18',
    date: null,
    month: null,
    time: null,
    datetime: null
  },
  '2025-03': {
    week: null,
    date: null,
    month: 'month: Y=2025, M=3, D=null, H=null, M=null, S=null, N=null, W=null',
    time: null,
    datetime: null
  },

  '2025-03-18 12:20': {
    week: null,
    date: null,
    month: null,
    time: null,
    datetime: 'datetime: Y=2025, M=3, D=18, H=12, M=20, S=NaN, N=NaN, W=null'
  },
  '2025-03-18 12:20:45': {
    week: null,
    date: null,
    month: null,
    time: null,
    datetime: 'datetime: Y=2025, M=3, D=18, H=12, M=20, S=45, N=NaN, W=null'
  },
  '2025-03-18 12:20:45.789': {
    week: null,
    date: null,
    month: null,
    time: null,
    datetime: 'datetime: Y=2025, M=3, D=18, H=12, M=20, S=45, N=789, W=null'
  },
  '2025-03-18T12:20': {
    week: null,
    date: null,
    month: null,
    time: null,
    datetime: 'datetime: Y=2025, M=3, D=18, H=12, M=20, S=NaN, N=NaN, W=null'
  },
  '2025-03-18T12:20:45': {
    week: null,
    date: null,
    month: null,
    time: null,
    datetime: 'datetime: Y=2025, M=3, D=18, H=12, M=20, S=45, N=NaN, W=null'
  },
  '2025-03-18T12:20:45.789': {
    week: null,
    date: null,
    month: null,
    time: null,
    datetime: 'datetime: Y=2025, M=3, D=18, H=12, M=20, S=45, N=789, W=null'
  }
};
for (const[string, expecteds] of Object.entries(datetimeParseData)) {
  for (const[type, expected] of Object.entries(expecteds)) {
    test('Datetime.parse: ' + string + ' AS ' + type, ()=>{
        var actual = createDatetime(type, string);
        if (expected === null) {
          expect(actual).toBeNull();
        } else {
          expect(actual.toString()).toStrictEqual(expected);
        }
    });
  }
}
/****************/
[
  [
    'Same', 'datetime',
    '2025-03-24 20:09:45.999', '2025-03-24 20:09:45.999', 0
  ],
  [
    'Year different 1', 'datetime',
    '2026-03-24 20:09:45.999', '2025-03-24 20:09:45.999', -1
  ],
  [
    'Year different 2', 'datetime',
    '2025-03-24 20:09:45.999', '2026-03-24 20:09:45.999', 1
  ],
  [
    'Month different 1', 'datetime',
    '2025-04-24 20:09:45.999', '2025-03-24 20:09:45.999', -1
  ],
  [
    'Month different 2', 'datetime',
    '2025-03-24 20:09:45.999', '2025-04-24 20:09:45.999', 1
  ],
  [
    'Day different 1', 'datetime',
    '2025-03-25 20:09:45.999', '2025-03-24 20:09:45.999', -1
  ],
  [
    'Day different 2', 'datetime',
    '2025-03-24 20:09:45.999', '2025-03-25 20:09:45.999', 1
  ],
  [
    'Hour different 1', 'datetime',
    '2025-03-24 21:09:45.999', '2025-03-24 20:09:45.999', -1
  ],
  [
    'Hour different 2', 'datetime',
    '2025-03-24 20:09:45.999', '2025-03-24 21:09:45.999', 1
  ],
  [
    'Minute different 1', 'datetime',
    '2025-03-24 20:10:45.999', '2025-03-24 20:09:45.999', -1
  ],
  [
    'Minute different 2', 'datetime',
    '2025-03-24 20:09:45.999', '2025-03-24 20:10:45.999', 1
  ],
  [
    'Second different 1', 'datetime',
    '2025-03-24 20:09:46.999', '2025-03-24 20:09:45.999', -1
  ],
  [
    'Second different 2', 'datetime',
    '2025-03-24 20:09:45.999', '2025-03-24 20:09:46.999', 1
  ],
  [
    'Nano different 1', 'datetime',
    '2025-03-24 20:09:45.999', '2025-03-24 20:09:45.998', -1
  ],
  [
    'Nano different 2', 'datetime',
    '2025-03-24 20:09:45.998', '2025-03-24 20:09:45.999', 1
  ],

  [
    'Week: same', 'week',
    '2025-W18', '2025-W18', 0
  ],
  [
    'Week: year different 1', 'week',
    '2026-W18', '2025-W18', -1
  ],
  [
    'Week: year different 2', 'week',
    '2025-W18', '2026-W18', 1
  ],
  [
    'Week: week different 1', 'week',
    '2025-W19', '2025-W18', -1
  ],
  [
    'Week: week different 2', 'week',
    '2025-W18', '2025-W19', 1
  ],
  [
    'Week: different 1', 'week',
    '2026-W18', '2025-W19', -1
  ],
  [
    'Week: different 2', 'week',
    '2025-W19', '2026-W18', 1
  ],
  [
    'Second with NaN 1', 'time',
    '20:09:00', '20:09', 0
  ],
  [
    'Second with NaN 2', 'time',
    '20:09', '20:09:00', 0
  ],
  [
    'Second with NaN 3', 'time',
    '20:09:46', '20:09', -1
  ],
  [
    'Second with NaN 3', 'time',
    '20:09', '20:09:46', 1
  ]
].forEach((data)=>{
  test('Timestamp.compare: ' + data[0], ()=>{
    var first = createDatetime(data[1], data[2]);
    var second = createDatetime(data[1], data[3]);
    expect(first.compare(second)).toBe(data[4]);
  });
});