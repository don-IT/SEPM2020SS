import {Enclosure} from './enclosure';

export class Animal {
  constructor(
    public id: number,
    public name: string,
    public description: string,
    public species: string,
    public enclosure: string,
    public publicInformation: string,
    ) {
  }
}
