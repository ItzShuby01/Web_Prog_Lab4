// Interface for the data received from the server (CalculationResultDTO)
export interface IPoint {
  x: number;
  y: number;
  r: number;
  hit: boolean;
  executionTime: string; // The LocalDateTime is converted to a string
}

// Interface for the data sent to the server (PointRequestDTO)
export interface PointRequest {
  x: number;
  y: number;
  r: number;
  source: string; //'canvas' | 'form'
}
