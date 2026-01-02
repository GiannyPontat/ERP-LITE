import { Injectable } from '@angular/core'


@Injectable ( {providedIn: 'root'} )
export class IdsService {

  ids : { [index : string] : number } = {}


  public has ( key : string ) {
    return key in this.ids
  }


  public set ( key : string, value : number ) {
    this.ids[key] = value
  }


  public get ( key : string ) : number {
    return this.ids[key]
  }


  public delete ( key : string ) {
    if (this.has ( key )) {
      delete this.ids[key]
      return true
    }
    return false
  }

}
