import React, { useState } from 'react';
import { Badge } from './ui/Badge';
import { dailyEvents } from '../data';

export const RouletteSimulator: React.FC = () => {
    const [simResult, setSimResult] = useState<any>(null);
    const [isSpinning, setIsSpinning] = useState(false);

    const spinRoulette = () => {
        if (isSpinning) return;
        setIsSpinning(true);
        setSimResult(null);
        
        let count = 0;
        const maxSpins = 20;
        const interval = setInterval(() => {
            const randomEvent = dailyEvents[Math.floor(Math.random() * dailyEvents.length)];
            setSimResult(randomEvent);
            count++;
            if (count >= maxSpins) {
                clearInterval(interval);
                setIsSpinning(false);
            }
        }, 80);
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-[50vh] animate-in zoom-in duration-300">
            <div className="text-center mb-8">
                <h2 className="text-4xl text-white mb-2 text-shadow">SIMULADOR DE RULETA</h2>
                <p className="text-xl text-gray-400">Prueba tu suerte antes de entrar al server...</p>
            </div>

            <div className="mc-card w-full max-w-md bg-black p-8 text-center min-h-[250px] flex items-center justify-center flex-col relative overflow-hidden ring-4 ring-red-900/50">
                {/* Decoration lines */}
                <div className="absolute top-0 left-0 w-full h-2 bg-gradient-to-r from-red-600 via-transparent to-red-600"></div>
                <div className="absolute bottom-0 left-0 w-full h-2 bg-gradient-to-r from-red-600 via-transparent to-red-600"></div>
                <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10"></div>

                {simResult ? (
                    <div className="animate-in bounce-in duration-500 z-10">
                        <div className="text-7xl mb-4 filter drop-shadow-[0_0_10px_rgba(255,255,255,0.3)]">{simResult.icon}</div>
                        <h3 className={`text-3xl mb-2 uppercase font-bold tracking-wider ${simResult.type === 'Maldición' ? 'text-red-500' : simResult.type === 'Bendición' ? 'text-green-500' : 'text-purple-500'}`}>
                            {simResult.name}
                        </h3>
                        <Badge type={simResult.type} />
                        <p className="mt-6 text-gray-300 text-xl border-t border-gray-800 pt-4">{simResult.desc}</p>
                    </div>
                ) : (
                    <div className="text-gray-600 text-2xl z-10">
                        {isSpinning ? "GIRANDO..." : "PRESIONA EL BOTÓN"}
                    </div>
                )}
            </div>

            <button 
                onClick={spinRoulette} 
                disabled={isSpinning}
                className="mc-btn mt-8 px-8 py-4 text-2xl text-white font-bold uppercase tracking-widest hover:brightness-110 disabled:opacity-50 disabled:cursor-not-allowed transform hover:-translate-y-1 active:translate-y-0 transition-all"
            >
                {isSpinning ? "Rodando..." : "TIRAR DADO"}
            </button>
            
            {simResult && (
                <p className="mt-4 text-gray-500 text-lg">Probabilidad: {(100/dailyEvents.length).toFixed(2)}%</p>
            )}
        </div>
    );
};
